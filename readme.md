# Spring AI RAG Demo

A simple Retrieval-Augmented Generation (RAG) application built with Spring AI, Ollama, and pgvector. Upload a PDF, ask questions about it — the app finds the relevant chunks and lets a local LLM answer them.

---

## What It Does

1. On startup, reads a PDF from the classpath (`gta_cheat_codes_master_list.pdf`)
2. Splits it into chunks using a token-based text splitter
3. Embeds each chunk via `nomic-embed-text` and stores them in pgvector (PostgreSQL)
4. Exposes a `/chat/prompt` endpoint — questions are answered by `deepseek-r1:1.5b` using the relevant chunks as context

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5 + Spring AI 1.1.7 |
| LLM & Embeddings | Ollama (local) |
| Chat model | `deepseek-r1:1.5b` |
| Embedding model | `nomic-embed-text` |
| Vector store | pgvector (PostgreSQL 16) |
| Document parsing | Apache Tika |
| Database | Docker (pgvector/pgvector:pg16) |

---

## Prerequisites

- Java 21
- Maven
- Docker & Docker Compose
- [Ollama](https://ollama.com) installed and running locally on port `11434`

---

## Getting Started

### 1. Start the database

```bash
docker-compose up -d
```

This starts a pgvector-enabled PostgreSQL instance on port `5432`. The schema is auto-initialized on first run (`initialize-schema: true`).

### 2. Add your PDF

Place your PDF under:

```
src/main/resources/docs/gta_cheat_codes_master_list.pdf
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

On startup, the app will:
- Pull `deepseek-r1:1.5b` and `nomic-embed-text` from Ollama if not already present
- Ingest and embed the PDF into pgvector

### 4. Ask a question

```bash
curl -X POST http://localhost:8080/chat/prompt \
  -H "Content-Type: application/json" \
  -d '"What are the cheat codes for GTA?"'
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/rag/demo/
│   │   ├── DemoApplication.java              # Entry point
│   │   ├── ingestion/
│   │   │   └── DocumentIngestionService.java # PDF → chunks → vector store
│   │   └── controller/
│   │       └── ChatController.java           # POST /chat/prompt
│   └── resources/
│       ├── application.yaml
│       └── docs/
│           └── gta_cheat_codes_master_list.pdf
docker-compose.yaml
```

---

## Configuration

Key settings in `application.yaml`:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat.options.model: deepseek-r1:1.5b
      embedding.options.model: nomic-embed-text
      init.pull-model-strategy: when_missing  # auto-downloads models
    vectorstore:
      pgvector:
        initialize-schema: true               # auto-creates the vector table
```

---

## How RAG Works Here

```
User question
     │
     ▼
Embed question (nomic-embed-text)
     │
     ▼
Similarity search in pgvector
     │
     ▼
Top-N relevant chunks injected as context
     │
     ▼
deepseek-r1:1.5b generates an answer
     │
     ▼
Response returned to user
```

The `QuestionAnswerAdvisor` from Spring AI handles the retrieval and context injection automatically.

---

## Notes

- The ingestion runs every time the application starts (`CommandLineRunner`). For production use, add a check to skip re-ingestion if documents are already stored.
- Ollama must be running before the application starts.
- Model download on first run may take a few minutes depending on your connection.