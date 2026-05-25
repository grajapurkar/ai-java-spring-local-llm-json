# Spring Boot Local LLM RAG POC

## Run

mvn clean install
mvn spring-boot:run

# Spring Boot Local LLM RAG Assistant

An enterprise-style AI-powered Spring Boot application demonstrating:

- Local LLM execution
- ONNX-based embeddings
- Retrieval-Augmented Generation (RAG)
- In-memory vector search
- Metadata-aware policy retrieval
- Enterprise AI architecture foundations
- Fully offline JVM-native execution

---

# 1. Introduction

This project is a Proof of Concept (POC) demonstrating how modern enterprise AI assistants can be implemented entirely within a Java Spring Boot application using local AI models and semantic retrieval techniques.

The application simulates an intelligent insurance policy assistant capable of:

- Understanding user questions
- Retrieving semantically relevant insurance policies
- Performing metadata-aware document retrieval
- Generating contextual AI responses
- Running fully offline without external AI APIs

The first version intentionally focuses on:

- simplicity
- local execution
- semantic search
- modular enterprise design
- JVM-native AI integration

This project serves as the foundational architecture for future enterprise AI systems involving:

- AI agents
- human governance
- hybrid search
- distributed vector databases
- event-driven AI workflows
- multi-agent orchestration

---

# 2. Problem Statement

Traditional enterprise applications rely heavily on:

- relational databases
- keyword-based search
- rigid workflows
- manual policy lookup

These approaches become increasingly inefficient when dealing with:

- large document repositories
- unstructured knowledge
- semantic understanding
- contextual retrieval
- conversational interfaces

This application demonstrates how semantic AI retrieval can solve these limitations using:

- embeddings
- vector search
- local LLMs
- metadata-aware retrieval

---

# 3. Application Goals

The primary goals of this POC are:

- Demonstrate local AI execution inside JVM
- Avoid dependency on cloud AI providers
- Build enterprise-style semantic retrieval
- Implement lightweight RAG architecture
- Enable metadata-aware AI search
- Create scalable AI-ready architecture
- Build foundation for enterprise copilots

---

# 4. Core Technologies

| Technology | Purpose |
|---|---|
| Java 21 | Modern JVM runtime |
| Spring Boot 3.4 | Application framework |
| ONNX Runtime | Local embedding inference |
| MiniLM ONNX | Semantic embeddings |
| DJL Tokenizers | HuggingFace tokenization |
| Maven | Build system |
| REST APIs | Chat interface |
| In-Memory Vector Store | Semantic retrieval |
| JUnit 5 | Testing |

---

# 5. High-Level Architecture

```text
+--------------------------------------------------+
|                Spring Boot App                   |
+--------------------------------------------------+
|                                                  |
|  REST API                                        |
|       ↓                                          |
|  Chat Controller                                 |
|       ↓                                          |
|  Chat Service                                    |
|       ↓                                          |
|  RAG Service                                     |
|       ↓                                          |
|  Embedding Service (MiniLM ONNX)                 |
|       ↓                                          |
|  InMemory Vector Store                           |
|                                                  |
+--------------------------------------------------+

# README.md — Spring Boot Local LLM RAG Assistant

````markdown
# Spring Boot Local LLM RAG Assistant

An enterprise-style AI-powered Spring Boot application demonstrating:

- Local LLM execution
- ONNX-based embeddings
- Retrieval-Augmented Generation (RAG)
- In-memory vector search
- Metadata-aware policy retrieval
- Enterprise AI architecture foundations
- Fully offline JVM-native execution

---

# 1. Introduction

This project is a Proof of Concept (POC) demonstrating how modern enterprise AI assistants can be implemented entirely within a Java Spring Boot application using local AI models and semantic retrieval techniques.

The application simulates an intelligent insurance policy assistant capable of:

- Understanding user questions
- Retrieving semantically relevant insurance policies
- Performing metadata-aware document retrieval
- Generating contextual AI responses
- Running fully offline without external AI APIs

The first version intentionally focuses on:

- simplicity
- local execution
- semantic search
- modular enterprise design
- JVM-native AI integration

This project serves as the foundational architecture for future enterprise AI systems involving:

- AI agents
- human governance
- hybrid search
- distributed vector databases
- event-driven AI workflows
- multi-agent orchestration

---

# 2. Problem Statement

Traditional enterprise applications rely heavily on:

- relational databases
- keyword-based search
- rigid workflows
- manual policy lookup

These approaches become increasingly inefficient when dealing with:

- large document repositories
- unstructured knowledge
- semantic understanding
- contextual retrieval
- conversational interfaces

This application demonstrates how semantic AI retrieval can solve these limitations using:

- embeddings
- vector search
- local LLMs
- metadata-aware retrieval

---

# 3. Application Goals

The primary goals of this POC are:

- Demonstrate local AI execution inside JVM
- Avoid dependency on cloud AI providers
- Build enterprise-style semantic retrieval
- Implement lightweight RAG architecture
- Enable metadata-aware AI search
- Create scalable AI-ready architecture
- Build foundation for enterprise copilots

---

# 4. Core Technologies

| Technology | Purpose |
|---|---|
| Java 21 | Modern JVM runtime |
| Spring Boot 3.4 | Application framework |
| ONNX Runtime | Local embedding inference |
| MiniLM ONNX | Semantic embeddings |
| DJL Tokenizers | HuggingFace tokenization |
| Maven | Build system |
| REST APIs | Chat interface |
| In-Memory Vector Store | Semantic retrieval |
| JUnit 5 | Testing |

---

# 5. High-Level Architecture

```text
+--------------------------------------------------+
|                Spring Boot App                   |
+--------------------------------------------------+
|                                                  |
|  REST API                                        |
|       ↓                                          |
|  Chat Controller                                 |
|       ↓                                          |
|  Chat Service                                    |
|       ↓                                          |
|  RAG Service                                     |
|       ↓                                          |
|  Embedding Service (MiniLM ONNX)                 |
|       ↓                                          |
|  InMemory Vector Store                           |
|                                                  |
+--------------------------------------------------+
````

---

# 6. Application Flow

```text
User Question
      ↓

REST API Request
      ↓

Generate Query Embedding
      ↓

Vector Similarity Search
      ↓

Retrieve Top Matching Policies
      ↓

Build AI Prompt
      ↓

Generate AI Response
      ↓

Return Contextual Answer
```

---

# 7. Current Version Scope

The current version focuses on:

## Included Features

* Text-based policy processing
* JSON-based policy knowledge base
* ONNX embedding generation
* Cosine similarity vector search
* Metadata-aware policy retrieval
* Local JVM-native execution
* REST chatbot endpoint
* Basic JUnit testing

---

## Excluded Features (Future Scope)

* Real LLM inference
* Kafka event streaming
* Qdrant vector database
* Multi-agent orchestration
* Human governance workflows
* Hybrid search pipelines
* OpenTelemetry tracing
* Distributed AI services

---

# 8. Why Local AI?

This application intentionally avoids cloud-hosted AI APIs.

## Benefits

* Full offline execution
* No external API cost
* Improved privacy
* Enterprise security alignment
* Faster local inference
* Easier enterprise deployment
* Better governance control

---

# 9. Why ONNX MiniLM?

The application uses:

## all-MiniLM-L6-v2

for semantic embeddings because:

* lightweight
* fast CPU inference
* excellent semantic quality
* small memory footprint
* ideal for enterprise JVM applications

---

# 10. Why RAG?

Retrieval-Augmented Generation (RAG) enables the assistant to:

* retrieve relevant enterprise knowledge
* enrich prompts with contextual data
* reduce hallucinations
* improve AI response quality

Instead of relying purely on AI generation, the assistant grounds responses using actual policy data.

---

# 11. Why Metadata-Aware Retrieval?

Enterprise AI systems require more than vector search.

This project embeds:

* category
* region
* customer type
* plan
* coverage
* addons

alongside policy content.

This improves:

* semantic recall
* contextual precision
* governance
* filtering
* agent routing

---

# 12. Policy Document Structure

Policies are stored as structured JSON documents.

Example:

```json
{
  "policyId":"POL-1001",
  "category":"Cyber",
  "plan":"Elite",
  "region":"Hyderabad",
  "customerType":"Corporate",
  "coverageLimit":"10 lakh INR",
  "duration":"3 years",
  "addons":["zero depreciation"],
  "claimChannel":"online portal",
  "content":"Cyber insurance protects against ransomware attacks."
}
```

---

# 13. Embedding Pipeline

The application performs:

```text
Policy JSON
      ↓

Metadata Enrichment
      ↓

Embedding Text Construction
      ↓

ONNX MiniLM Inference
      ↓

384-Dimensional Vector
      ↓

Vector Store
```

---

# 14. Vector Search

The vector store uses:

## Cosine Similarity

to compare:

* query embeddings
* policy embeddings

and retrieve semantically similar policies.

---

# 15. Project Structure

```text
src/main/java/com/example/aipoc

├── controller
├── service
├── config
├── vector
├── model
├── util
└── test
```

---

# 16. REST API

## Endpoint

```http
POST /api/chat
```

---

## Request

```json
{
  "question":"Does insurance cover flood damage?"
}
```

---

## Response

```json
{
  "answer":"Flood damage claims are supported under comprehensive insurance plans."
}
```

---

# 17. Running the Application

## Step 1

Build project:

```bash
mvn clean install
```

---

## Step 2

Run application:

```bash
mvn spring-boot:run
```

---

## Step 3

Test endpoint:

```bash
curl --location 'http://localhost:8080/api/chat' \
--header 'Content-Type: application/json' \
--data '{
  "question":"Does insurance cover flood damage?"
}'
```

---

## Developer quickstart (dev profile)

- The project includes a `dev` profile that limits policy ingestion for faster local iteration. `src/main/resources/application-dev.yml` contains overrides for `ai.loader.enabled` and `ai.loader.maxLines` (defaults: enabled=true, maxLines=20).

- Recommended (Windows PowerShell): use the helper script which builds the project, starts Spring Boot with the `dev` profile, waits for startup/log markers and issues a sample POST.

```powershell
./scripts/run-dev.ps1
```

- Manual alternative:

```powershell
mvn -Dspring-boot.run.profiles=dev spring-boot:run
```

- Notes:
  - The helper script waits for either the Spring Boot "Started AiPocApplication" marker or the loader marker "Policies loaded into vector store". If the loader is disabled the script will still proceed once Spring Boot has started.
  - To completely skip ingestion (fastest start), set `ai.loader.enabled=false` in `application-dev.yml` or override via JVM property: `-Dai.loader.enabled=false`.
  - To change the sample size loaded at startup set `ai.loader.maxLines` in `application-dev.yml`.


# 18. Unit Testing

Run tests:

```bash
mvn test
```

---

# 19. Current Limitations

The current version intentionally simplifies several areas:

| Limitation        | Reason                 |
| ----------------- | ---------------------- |
| Mocked LLM        | Simpler POC            |
| In-memory vectors | Avoid external DB      |
| No hybrid search  | Initial implementation |
| No governance     | Future enhancement     |
| No streaming      | Reduced complexity     |

---

# 20. Future Enhancements

## Planned Features

* Local LLM integration
* Ollama support
* Qdrant vector database
* Kafka event streaming
* AI agents
* Human governance
* Hybrid retrieval
* Metadata filtering
* Distributed AI orchestration
* Observability and tracing

---

# 21. Enterprise Evolution Path

```text
Text Policies
      ↓

JSON Policies
      ↓

Metadata Embeddings
      ↓

Hybrid Search
      ↓

Vector Database
      ↓

AI Agents
      ↓

Human Governance
      ↓

Enterprise AI Platform
```

---

# 22. Educational Purpose

This project is designed to help developers understand:

* semantic AI retrieval
* local AI execution
* enterprise RAG design
* embedding pipelines
* vector search systems
* AI-ready Spring Boot architectures

---

# 23. Key Learning Outcomes

After exploring this project, developers will understand:

* how embeddings work
* how semantic search works
* how vector retrieval works
* how RAG systems are designed
* how local AI can run inside JVM
* how enterprise AI architectures evolve

---

# 24. Recommended Next Steps

Recommended next upgrades:

1. Replace mocked LLM with TinyLlama
2. Add metadata-aware filtering
3. Introduce Qdrant
4. Add hybrid retrieval
5. Add governance workflows
6. Add AI agents
7. Add observability
8. Add distributed deployment

---

# 25. Conclusion

This project demonstrates how modern enterprise AI systems can be built incrementally using:

* Spring Boot
* local AI models
* semantic retrieval
* embeddings
* vector search
* structured knowledge

The current implementation forms a strong foundation for future enterprise-grade AI assistants, copilots, and intelligent knowledge systems.

---

```
```
