API de Autenticação para Aplicativo Mobile
Esta é uma API Spring Boot com sistema de login e cadastro, integrada ao MongoDB, desenvolvida para servir como backend para um aplicativo mobile.

📋 Requisitos
Java 17+

Maven

MongoDB (local ou remoto)

🚀 Como Executar
Clone o repositório:

bash
Copy
git clone https://github.com/seu-usuario/api-mobile-auth.git
cd api-mobile-auth
Configure o MongoDB:

Certifique-se de ter o MongoDB rodando localmente ou

Atualize a URI no application.properties:

properties
Copy
spring.data.mongodb.uri=mongodb://seu-usuario:senha@servidor:porta/banco
Execute a aplicação:

bash
Copy
mvn spring-boot:run
Ou importe o projeto em sua IDE favorita e execute a classe MobileAppApplication.

🔐 Endpoints da API
Cadastro de Usuário
POST /api/auth/signup

Body:

json
Copy
{
  "username": "novousuario",
  "email": "usuario@email.com",
  "password": "senhasecreta"
}
Login
POST /api/auth/login

Body:

json
Copy
{
  "username": "novousuario",
  "password": "senhasecreta"
}
Resposta:

json
Copy
{
  "token": "token-de-autenticacao",
  "username": "novousuario"
}
🛠️ Estrutura do Projeto
Copy
src/
├── main/
│   ├── java/com/example/mobileapp/
│   │   ├── config/          # Configurações (Segurança, etc.)
│   │   ├── controller/      # Controladores da API
│   │   ├── dto/             # Objetos de transferência de dados
│   │   ├── model/           # Entidades do banco de dados
│   │   ├── repository/      # Repositórios do MongoDB
│   │   ├── service/         # Lógica de negócio
│   │   └── MobileAppApplication.java  # Classe principal
│   └── resources/
│       └── application.properties  # Configurações da aplicação
🔧 Tecnologias Utilizadas
Spring Boot: Framework para desenvolvimento Java

Spring Security: Autenticação e autorização

Spring Data MongoDB: Integração com banco de dados MongoDB

Maven: Gerenciamento de dependências

📌 Próximos Passos (Melhorias Planejadas)
Implementar autenticação com JWT

Adicionar confirmação por e-mail

Criar sistema de recuperação de senha

Adicionar mais campos ao perfil do usuário

Implementar upload de foto de perfil

🤝 Contribuição
Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

📄 Licença
Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.
