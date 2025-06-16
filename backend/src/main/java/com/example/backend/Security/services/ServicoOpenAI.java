package com.example.backend.Security.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Service
public class ServicoOpenAI {
    private static final Logger logger = LoggerFactory.getLogger(ServicoOpenAI.class);
    private static final int MAX_RETRIES = 3;
    private static final int MAX_PARALLEL_REQUESTS = 5;
    private static final int MAX_FLASHCARDS_PER_BATCH = 5;

    private final OpenAiService servico;
    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;

    public ServicoOpenAI(
            @Value("${openai.api.key}") String chaveApi,
            @Value("${openai.timeout.seconds:60}") int timeoutSeconds) {
        this.servico = new OpenAiService(chaveApi, Duration.ofSeconds(timeoutSeconds));
        this.executorService = Executors.newFixedThreadPool(MAX_PARALLEL_REQUESTS);
        this.objectMapper = new ObjectMapper();
    }

    // ========== MÉTODOS PÚBLICOS PRINCIPAIS ==========

    /**
     * Processa uma lista de flashcards e gera questões de quiz
     */
    public List<Map<String, Object>> processarFlashcardsEmLote(List<Map<String, String>> flashcards) {
        // Dividir em batches para evitar sobrecarga
        List<List<Map<String, String>>> batches = partitionList(flashcards, MAX_FLASHCARDS_PER_BATCH);

        return batches.stream()
                .flatMap(batch -> {
                    try {
                        return processarBatchAsync(batch).join().stream();
                    } catch (Exception e) {
                        logger.error("Erro ao processar batch de flashcards", e);
                        return batch.stream()
                                .map(this::gerarFallbackParaFlashcard);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Gera uma variação de pergunta mantendo o significado original
     */
    public String gerarVariacaoPergunta(String perguntaOriginal) {
        String prompt = """
            Parafraseie a seguinte pergunta mantendo:
            - O significado exato
            - O nível técnico
            - O contexto original
            
            Regras:
            1. Não altere termos técnicos
            2. Mantenha a mesma profundidade
            3. Use no máximo 20 palavras
            
            Pergunta original: "%s"
            
            Retorne APENAS a nova versão da pergunta, sem comentários ou formatação adicional.
            """.formatted(perguntaOriginal);

        return chamarAPIComRetry(prompt).trim();
    }

    /**
     * Gera uma variação de resposta mantendo a precisão técnica
     */
    public String gerarVariacaoResposta(String respostaOriginal) {
        String prompt = """
            Parafraseie a seguinte resposta mantendo:
            - A precisão técnica
            - O significado exato
            - O contexto original
            
            Regras:
            1. Não altere termos técnicos
            2. Mantenha o mesmo nível de detalhe
            3. Use no máximo 15 palavras
            
            Resposta original: "%s"
            
            Retorne APENAS a nova versão da resposta, sem comentários ou formatação adicional.
            """.formatted(respostaOriginal);

        return chamarAPIComRetry(prompt).trim();
    }

    /**
     * Gera distratores (alternativas incorretas) para uma questão
     */
    public List<String> gerarDistratores(String pergunta, String respostaCorreta, int quantidade) {
        String prompt = String.format("""
            Para a pergunta: '%s'
            Resposta correta: '%s'
            
            Gere %d alternativas incorretas que sejam:
            - Relacionadas ao mesmo tema
            - Plausíveis, mas claramente erradas
            - Com nível de complexidade similar à resposta correta
            - Diferentes entre si
            - Não contenham a resposta correta
            
            Formato de saída (uma por linha):
            1) Alternativa 1
            2) Alternativa 2
            ...
            """, pergunta, respostaCorreta, quantidade);

        String resposta = chamarAPIComRetry(prompt);
        return extrairAlternativas(resposta, quantidade);
    }

    /**
     * Gera explicação para uma questão
     */
    public String gerarExplicacao(String pergunta, String respostaCorreta, String respostaUsuario, boolean acertou) {
        String prompt = String.format("""
            Para a pergunta: '%s'
            Resposta correta: '%s'
            Resposta do usuário: '%s'
            
            Gere uma explicação %s que:
            1. Explique o conceito de forma clara e didática
            2. Destaque porque a resposta está %s
            3. Ofereça contexto adicional relevante
            4. Seja concisa (2-3 frases)
            
            %s
            """, 
            pergunta,
            respostaCorreta,
            respostaUsuario,
            acertou ? "positiva" : "corretiva",
            acertou ? "correta" : "incorreta",
            acertou ? "Parabenize pelo acerto." : "Explique qual seria a resposta adequada.");

        return chamarAPIComRetry(prompt).trim();
    }

    // ========== MÉTODOS PRIVADOS ==========

    @Async
    private CompletableFuture<List<Map<String, Object>>> processarBatchAsync(List<Map<String, String>> batch) {
        List<CompletableFuture<Map<String, Object>>> futures = batch.stream()
                .map(flashcard -> CompletableFuture.supplyAsync(
                        () -> processarFlashcardIndividual(flashcard),
                        executorService))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    private Map<String, Object> processarFlashcardIndividual(Map<String, String> flashcard) {
        try {
            String pergunta = flashcard.get("pergunta");
            String resposta = flashcard.get("resposta");

            // Otimização: Faz tudo em uma única chamada para a API
            String prompt = String.format("""
                Para o flashcard:
                Pergunta: %s
                Resposta: %s
                
                Gere em formato JSON com as seguintes chaves:
                1. "pergunta_variacao" (uma variação da pergunta)
                2. "resposta_variacao" (uma variação da resposta)
                3. "distratores" (array com 3 alternativas incorretas)
                4. "explicacao" (explicação para resposta correta)
                
                Requisitos:
                - Mantenha o significado original
                - Preserve termos técnicos
                - Seja conciso
                """, pergunta, resposta);

            String respostaJson = chamarAPIComRetry(prompt);
            return parseRespostaJson(respostaJson);
        } catch (Exception e) {
            logger.error("Erro ao processar flashcard individual", e);
            return gerarFallbackParaFlashcard(flashcard);
        }
    }

    private Map<String, Object> parseRespostaJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Map.class);
    }

    private Map<String, Object> gerarFallbackParaFlashcard(Map<String, String> flashcard) {
        // Implementação de fallback simples quando a API falha
        return Map.of(
                "pergunta_original", flashcard.get("pergunta"),
                "pergunta_variacao", flashcard.get("pergunta"),
                "resposta_original", flashcard.get("resposta"),
                "resposta_variacao", flashcard.get("resposta"),
                "distratores", Collections.emptyList(),
                "explicacao", "Explicação não disponível no momento",
                "fallback", true
        );
    }

    private String chamarAPIComRetry(String prompt) {
        AtomicInteger tentativas = new AtomicInteger(0);
        while (tentativas.get() < MAX_RETRIES) {
            try {
                return chamarAPI(prompt);
            } catch (Exception e) {
                tentativas.incrementAndGet();
                if (tentativas.get() >= MAX_RETRIES) {
                    throw new RuntimeException("Falha após " + MAX_RETRIES + " tentativas", e);
                }
                esperarComBackoff(tentativas.get());
            }
        }
        throw new RuntimeException("Não foi possível completar a operação");
    }

    private String chamarAPI(String prompt) {
        ChatCompletionRequest requisicao = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(
                        new ChatMessage("system", "Você é um assistente especializado em educação e criação de materiais didáticos."),
                        new ChatMessage("user", prompt)
                ))
                .temperature(0.7)
                .maxTokens(300)
                .build();

        return servico.createChatCompletion(requisicao)
                .getChoices().get(0)
                .getMessage().getContent();
    }

    private List<String> extrairAlternativas(String resposta, int quantidade) {
        return Arrays.stream(resposta.split("\n"))
                .limit(quantidade)
                .map(linha -> linha.replaceAll("^\\d+\\)\\s*", "").trim())
                .filter(alternativa -> !alternativa.isEmpty())
                .collect(Collectors.toList());
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    private void esperarComBackoff(int tentativa) {
        try {
            long delay = (long) Math.pow(2, tentativa) * 1000; // Backoff exponencial
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrompido durante espera", e);
        }
    }

    // Método para desligar o executor service quando o bean for destruído
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}