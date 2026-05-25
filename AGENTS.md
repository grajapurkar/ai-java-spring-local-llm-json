# AGENTS: How to be productive in this repository

Checklist for an AI coding agent (what I'll do first):
- Read these files: `DataLoaderConfig.java`, `EmbeddingService.java`, `InMemoryVectorStore.java`, `RagService.java`, `ChatService.java`, `LocalLlmService.java`, `PromptBuilder.java`, `ChatController.java`, `application.yml`, `pom.xml`.
- Confirm resources exist under `src/main/resources/model` (`model.onnx`, `tokenizer.json`) and policy files (`policies.txt`).
- Run build and a quick smoke run: `mvn clean install` then `mvn spring-boot:run` and call the POST `/api/chat` endpoint.

Quick summary (big picture)
- This is a small Spring Boot POC implementing a local RAG pipeline:
  - `ChatController` (REST) → `ChatService` (orchestration) → `RagService` (retrieval) → `EmbeddingService` (ONNX + tokenizer) → `InMemoryVectorStore` (cosine similarity search).
  - `LocalLlmService` is a placeholder LLM: it returns the built prompt wrapped in a small header/footer (see `LocalLlmService.generate`).
  - At startup `DataLoaderConfig` loads `policies.txt` line-by-line, generates embeddings via `EmbeddingService`, and adds `VectorDocument` entries to the singleton `InMemoryVectorStore` (@PostConstruct).

Concrete, discoverable patterns and examples
- Startup data load: `DataLoaderConfig.loadPolicies()` reads `/policies.txt` from resources and calls `embeddingService.generateEmbedding(line)` for each non-blank line, then `vectorStore.add(document)`. If `policies.txt` is large this will run at application startup and may be slow.
- Embedding pipeline: `EmbeddingService.init()` loads `/model/model.onnx` and `/model/tokenizer.json` from resources. If either resource is missing an exception is thrown (`RuntimeException("model.onnx not found")`). Tokenization uses DJL `HuggingFaceTokenizer` and ONNX Runtime `OrtSession` to run the model. Mean pooling across token embeddings is applied in `meanPooling(...)`.
- Vector search: `InMemoryVectorStore.similaritySearch(queryEmbedding, topK)` sorts all stored `VectorDocument` entries by cosine similarity and returns top K. The comparator uses `-cosineSimilarity(...)` to sort descending.
- Prompt construction: `PromptBuilder.build(context, question)` composes the prompt by concatenating the retrieved context and the question.
- Retrieval flow: `RagService.retrieve(question)` generates a query embedding, asks `vectorStore` for the top 3 matches (`topK=3`) and concatenates `VectorDocument.getContent()` entries with newlines.

Project-specific developer workflows
- Build: `mvn clean install`
- Run: `mvn spring-boot:run`
- Tests: `mvn test` (note: `ChatServiceTest` is present but commented out)
- Smoke test example (curl):

  curl --location 'http://localhost:8080/api/chat' \
  --header 'Content-Type: application/json' \
  --data '{"question":"Does insurance cover flood damage?"}'

What to check if things break (practical tips)
- Missing model/tokenizer: `EmbeddingService.init()` will throw. Confirm `src/main/resources/model/model.onnx` and `tokenizer.json` exist and are packaged into the JAR. Running `mvn package` and inspecting the generated JAR can verify resource packaging.
- Slow startup: the `@PostConstruct` in `DataLoaderConfig` will compute embeddings for every policy in `policies.txt`. To iterate faster, temporarily replace `policies.txt` with a small sample or comment out the `@PostConstruct` method.
- Platform native libs: ONNX Runtime JNI/native loading is managed by the `onnxruntime` dependency in `pom.xml`. If you see native library errors on your OS, ensure the proper onnxruntime native artifact is available for your platform (this repo relies on the maven dependency to supply native libs).

Small code-edit examples agents will commonly do
- Change retrieval size: edit `RagService.retrieve` to pass a different topK to `vectorStore.similaritySearch(...)`.
- Swap to a mocked embedding for faster iteration: replace `EmbeddingService.generateEmbedding` body with a deterministic pseudo-random vector (commented-out mock exists at file end of `EmbeddingService.java`).
- Replace placeholder LLM: edit `LocalLlmService.generate` to call a real local LLM runner or an external API. Currently it simply returns the prompt wrapped in a banner.

Files to read first (prioritized)
- src/main/java/com/example/aipoc/config/DataLoaderConfig.java  (startup data ingestion)
- src/main/java/com/example/aipoc/service/EmbeddingService.java (ONNX + tokenizer details)
- src/main/java/com/example/aipoc/vector/InMemoryVectorStore.java (similarity & ranking)
- src/main/java/com/example/aipoc/service/RagService.java (retrieval orchestration)
- src/main/java/com/example/aipoc/service/ChatService.java + controller (end-to-end entrypoint)
- src/main/resources/model/* (model.onnx + tokenizer.json)
- src/main/resources/policies.txt (policy lines used as documents)
- pom.xml (dependencies: onnxruntime, ai.djl tokenizers, spring-boot starter web)

Non-obvious conventions / idiosyncrasies
- Documents are stored as single-line entries in `policies.txt` and treated verbatim as the document content when building embeddings (no JSON parsing in loader). If you want structured fields, change the loader.
- VectorDocument.embedding is a Java float[]; comparisons assume same dimension as model output (384 per `application.yml`). There's no runtime check that stored vectors match query sizes.
- Cosine similarity uses simple double math (no epsilon checks). Very small numerical edge cases are not handled.

Quick debugging checklist for agents modifying runtime behavior
- Rebuild with `mvn clean package` after changing resources or Java code.
- Use `System.out.println` messages already present in `EmbeddingService` to confirm resource paths and tokenizer loading.
- To reproduce an issue locally: run the app, then call POST `/api/chat` with a simple question; inspect log output showing "Policies loaded into vector store" and "Tokenizer Loaded Successfully" for successful startup.

Where to make the smallest change to return faster results
- Edit `DataLoaderConfig.loadPolicies()` to only load N lines (for fast iteration) or add a property-driven guard around the @PostConstruct loading.

End of AGENTS guidance.

