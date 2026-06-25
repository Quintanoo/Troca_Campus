# TrocaCampus Backend

Backend do aplicativo TrocaCampus, desenvolvido para a disciplina de Desenvolvimento Mobile.

O projeto fornece uma API REST para autenticação, cadastro de produtos, fotos, propostas de troca, mensagens, avaliações e perfil de usuários.

## Tecnologias utilizadas

- Node.js
- TypeScript
- Express
- PostgreSQL
- Prisma ORM
- JWT
- Bcrypt
- Multer

## Pré-requisitos

Antes de rodar o projeto, é necessário ter instalado:

- Node.js
- PostgreSQL
- npm
- Git

## Como rodar o projeto

### 1. Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO
cd troca-campus-backend
```

### 2. Instalar dependências

```bash
npm install
```

### 3. Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto, seguindo o exemplo do `.env.example`:

```env
PORT=3333
DATABASE_URL="postgresql://postgres:SUA_SENHA@localhost:5432/troca_campus"
JWT_SECRET="troca_campus_secret"
```

### 4. Criar o banco de dados

Crie um banco PostgreSQL chamado:

```txt
troca_campus
```

### 5. Rodar as migrations

```bash
npx prisma migrate dev
```

### 6. Rodar o seed de categorias

```bash
npx prisma db seed
```

Esse comando cria categorias iniciais como:

- Livros
- Eletrônicos
- Materiais de Estudo
- Roupas
- Móveis
- Outros

### 7. Iniciar o servidor

```bash
npm run dev
```

A API ficará disponível em:

```txt
http://localhost:3333
```

## Health check

Para verificar se a API está funcionando:

```txt
GET http://localhost:3333/health
```

Resposta esperada:

```json
{
  "status": "ok",
  "message": "API TrocaCampus funcionando corretamente.",
  "timestamp": "..."
}
```

## URL para Android

No Postman local, use:

```txt
http://localhost:3333
```

No emulador Android, use:

```txt
http://10.0.2.2:3333
```

Isso é necessário porque, dentro do emulador, `localhost` aponta para o próprio emulador, não para o computador que está rodando o backend.

## Scripts disponíveis

```bash
npm run dev
```

Inicia o servidor em ambiente de desenvolvimento.

```bash
npm run build
```

Gera a versão compilada do projeto.

```bash
npm start
```

Executa a versão compilada.

```bash
npx prisma studio
```

Abre uma interface visual para visualizar os dados do banco.

## Principais módulos da API

- Autenticação de usuários
- Categorias
- Produtos/anúncios
- Fotos dos produtos
- Propostas de troca
- Mensagens entre usuários
- Avaliações e reputação
- Perfil e histórico do usuário

## Observações

O arquivo `.env` não deve ser enviado para o GitHub.

Use o arquivo `.env.example` como referência para configurar o ambiente local.