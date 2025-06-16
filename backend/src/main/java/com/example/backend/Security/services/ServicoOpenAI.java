package com.example.backend.Security.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Service
public class ServicoOpenAI {
    private final OpenAiService servico;

    public ServicoOpenAI(@Value("${openai.api.key}") String chaveApi) {
        this.servico = new OpenAiService(chaveApi, Duration.ofSeconds(30));
    }

    // Método para gerar variações de perguntas
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

        return chamarAPI(prompt).trim();
    }

    // Método para gerar variações de respostas
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

        return chamarAPI(prompt).trim();
    }

    // Método para gerar distratores (alternativas incorretas)
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

        String resposta = chamarAPI(prompt);
        return extrairAlternativas(resposta, quantidade);
    }

    // Método para gerar explicações
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

        return chamarAPI(prompt).trim();
    }

    private String chamarAPI(String prompt) {
        ChatCompletionRequest requisicao = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(List.of(
                new ChatMessage("system", "Você é um assistente especializado em educação e criação de materiais didáticos."),
                new ChatMessage("user", prompt)
            ))
            .temperature(0.7)  // Balanceia criatividade e precisão
            .maxTokens(300)
            .build();

        return servico.createChatCompletion(requisicao)
            .getChoices().get(0)
            .getMessage().getContent();
    }

    private List<String> extrairAlternativas(String resposta, int quantidade) {
        String[] linhas = resposta.split("\n");
        List<String> alternativas = new ArrayList<>();
        
        for (String linha : linhas) {
            if (alternativas.size() >= quantidade) break;
            
            // Remove numeração (1), 2), etc.) e espaços
            String alternativa = linha.replaceAll("^\\d+\\)\\s*", "").trim();
            if (!alternativa.isEmpty()) {
                alternativas.add(alternativa);
            }
        }
        
        // Garante que retorne exatamente a quantidade solicitada
        return alternativas.subList(0, Math.min(quantidade, alternativas.size()));
    }
}