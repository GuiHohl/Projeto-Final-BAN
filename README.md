# INSTRUÇÕES PARA COMPILAÇÃO E EXECUÇÃO  
## Sistema de Gerenciamento de Restaurante (MongoDB)

Este sistema foi desenvolvido em **Java puro**, com acesso ao banco de dados **MongoDB** utilizando o **driver oficial MongoDB Java Sync**, e interface de texto via console.  
O projeto utiliza **Maven** para automação do processo de build.

---

## Pré-requisitos

- Java JDK 17 ou superior instalado.  
- MongoDB Atlas (recomendado) ou uma instância local do MongoDB.  
- Uma IDE como IntelliJ IDEA ou Eclipse.

---

## Configuração do Projeto (Maven)

1. Clone ou baixe este repositório.  
2. Certifique-se de que o arquivo `pom.xml` esteja presente e com as dependências corretas.  
3. O Maven se encarregará automaticamente do download das dependências.

---

## Configuração do Banco de Dados (MongoDB)

### Opção 1 – MongoDB Atlas (nuvem)

1. Crie uma conta gratuita em [https://www.mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas).  
2. Crie um cluster e um banco de dados chamado `restauranteDB` (ou outro nome, e atualize a URI no código).  
3. Copie a URI de conexão, por exemplo:  
`mongodb+srv://usuario:senha@cluster0.mongodb.net/restauranteDB`
4. Insira essa URI no arquivo:  
`src/main/java/db/ConnectionFactory.java`

### Opção 2 – MongoDB local

1. Instale o MongoDB localmente: [https://www.mongodb.com/try/download/community](https://www.mongodb.com/try/download/community)  
2. Inicie o servidor local.  
3. Utilize a URI de conexão padrão:  
`mongodb://localhost:27017`
4. Atualize a URI no arquivo:  
`src/main/java/db/ConnectionFactory.java`

### Backup do Banco de Dados

- O backup será efetuado automaticamente ao se inicializar o projeto.  
- Caso deseje acessar ou utilizar os arquivos manualmente, os arquivos `.json` para backup estão localizados em:  
`src/main/resources/backup`

---

## Como Rodar o Projeto

1. Acesse o arquivo:  
`src/main/java/Main.java`  
2. Execute este arquivo pela sua IDE.  
3. O sistema será inicializado e executará automaticamente o backup do banco de dados em formato JSON.

---

## Estrutura Esperada no MongoDB

O sistema utiliza as seguintes coleções:

- `comandas`
- `pedidos`
- `produtos`
- `categorias`
- `adicionais`
- `funcionarios`
- `cargos`
- `pagamentos`

---

## Desenvolvedores

- Emanuelle de Toledo Medeiros  
- Guilherme Weber Hohl


