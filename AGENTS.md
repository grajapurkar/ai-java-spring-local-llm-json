# AGENTS: How to be productive in this repository

Checklist for an AI coding agent (what I'll do first):
- Read these files: `DataLoaderConfig.java`, `EmbeddingService.java`, `InMemoryVectorStore.java`, `RagService.java`, `ChatService.java`, `LocalLlmService.java`, `PromptBuilder.java`, `ChatController.java`, `application.yml`, `pom.xml`.
- Confirm resources exist under `src/main/resources/model` (`model.onnx`, `tokenizer.json`) and policy files (`policies.json`).
- Run build and a quick smoke run: `mvn clean install` then `mvn spring-boot:run` and call the POST `/api/chat` endpoint.

Quick summary (big picture)
- This is a small Spring Boot POC implementing a local RAG pipeline:
  - `ChatController` (REST) → `ChatService` (orchestration) → `RagService` (retrieval) → `EmbeddingService` (ONNX + tokenizer) → `InMemoryVectorStore` (cosine similarity search).
  - `LocalLlmService` is a placeholder LLM: it returns the built prompt wrapped in a small header/footer (see `LocalLlmService.generate`).
    -- At startup `DataLoaderConfig` loads `policies.json` (a JSON array of policy objects), builds a concatenated embedding text from `PolicyDocument` fields, generates embeddings via `EmbeddingService`, and adds `PolicyDocument` entries to the singleton `InMemoryVectorStore` (@PostConstruct).

Concrete, discoverable patterns and examples
-- Startup data load: `DataLoaderConfig.loadPolicies()` reads `/policies.json` from resources (a JSON array of `PolicyDocument` objects), builds an embedding input string from the policy's fields (category, plan, region, customerType, coverageLimit, duration, addons, claimChannel, content), calls `embeddingService.generateEmbedding(...)` for each policy and then `vectorStore.add(policy)`. If the policy file is large this will run at application startup and may be slow.
- Embedding pipeline: `EmbeddingService.init()` loads `/model/model.onnx` and `/model/tokenizer.json` from resources. If either resource is missing an exception is thrown (`RuntimeException("model.onnx not found")`). Tokenization uses DJL `HuggingFaceTokenizer` and ONNX Runtime `OrtSession` to run the model. Mean pooling across token embeddings is applied in `meanPooling(...)`.
-- Vector search: `InMemoryVectorStore.similaritySearch(queryEmbedding, topK)` sorts all stored `PolicyDocument` entries by cosine similarity (comparing `queryEmbedding` with `PolicyDocument.getEmbedding()`) and returns top K. The comparator uses `-cosineSimilarity(...)` to sort descending.
 - Prompt construction: `PromptBuilder.build(context, question)` composes the prompt by concatenating the retrieved context and the question.
 -- Retrieval flow: `RagService.retrieve(question)` generates a query embedding and asks `vectorStore` for the top matches (default `topK=5`). The service returns a `List<PolicyDocument>`; `ChatService` composes the context string by concatenating `PolicyDocument.getContent()` entries (joined with newlines) before building the prompt.

Project-specific developer workflows
- Build: `mvn clean install`
- Run: `mvn spring-boot:run`
-- Tests: `mvn test` (note: some unit tests in this repo are located under `src/main/test` and may assume older API shapes — review tests if you see signature mismatches)
- Smoke test example (curl):

  curl --location 'http://localhost:8080/api/chat' \
  --header 'Content-Type: application/json' \
  --data '{"question":"Does insurance cover flood damage?"}'

What to check if things break (practical tips)
- Missing model/tokenizer: `EmbeddingService.init()` will throw. Confirm `src/main/resources/model/model.onnx` and `tokenizer.json` exist and are packaged into the JAR. Running `mvn package` and inspecting the generated JAR can verify resource packaging.
- Slow startup: the `@PostConstruct` in `DataLoaderConfig` will compute embeddings for every policy in `policies.json`. To iterate faster, temporarily replace `policies.json` with a small sample or comment out the `@PostConstruct` method.
- Platform native libs: ONNX Runtime JNI/native loading is managed by the `onnxruntime` dependency in `pom.xml`. If you see native library errors on your OS, ensure the proper onnxruntime native artifact is available for your platform (this repo relies on the maven dependency to supply native libs).

Small code-edit examples agents will commonly do
- Change retrieval size: edit `RagService.retrieve` to pass a different topK to `vectorStore.similaritySearch(...)`.
- Swap to a mocked embedding for faster iteration: replace `EmbeddingService.generateEmbedding` body with a deterministic pseudo-random vector (commented-out mock exists at file end of `EmbeddingService.java`).
- Replace placeholder LLM: edit `LocalLlmService.generate` to call a real local LLM runner or an external API. Currently it simply returns the prompt wrapped in a banner.

-- Files to read first (prioritized)
- src/main/java/com/grp/aiapp/local/llm/config/DataLoaderConfig.java  (startup data ingestion; reads `policies.json` and maps into `PolicyDocument`)
- src/main/java/com/grp/aiapp/local/llm/service/EmbeddingService.java (ONNX + tokenizer details)
- src/main/java/com/grp/aiapp/local/llm/vector/InMemoryVectorStore.java (similarity & ranking; stores `PolicyDocument`)
  - src/main/java/com/grp/aiapp/local/llm/service/RagService.java (retrieval orchestration; returns a List<PolicyDocument>; uses topK=5)
- src/main/java/com/grp/aiapp/local/llm/service/ChatService.java + controller (end-to-end entrypoint)
- src/main/resources/model/* (model.onnx + tokenizer.json)
- src/main/resources/policies.json (policy objects used as documents)
- pom.xml (dependencies: onnxruntime, ai.djl tokenizers, spring-boot starter web)

Non-obvious conventions / idiosyncrasies
- Documents are represented as structured `PolicyDocument` objects loaded from `policies.json`; `DataLoaderConfig` composes an embedding input string from multiple fields (category, plan, region, customerType, coverageLimit, duration, addons, claimChannel, content). If you want the old single-line behavior, replace the loader input or change `DataLoaderConfig`.
- VectorDocument.embedding is a Java float[]; comparisons assume same dimension as model output (384 per `application.yml`). There's no runtime check that stored vectors match query sizes.
- Cosine similarity uses simple double math (no epsilon checks). Very small numerical edge cases are not handled.

Quick debugging checklist for agents modifying runtime behavior
- Rebuild with `mvn clean package` after changing resources or Java code.
- Use `System.out.println` messages already present in `EmbeddingService` to confirm resource paths and tokenizer loading.
- To reproduce an issue locally: run the app, then call POST `/api/chat` with a simple question; inspect log output showing "Policies loaded into vector store" and "Tokenizer Loaded Successfully" for successful startup.

Where to make the smallest change to return faster results
- Edit `DataLoaderConfig.loadPolicies()` to only load N entries (for fast iteration) or add a property-driven guard around the @PostConstruct loading. `DataLoaderConfig` now consumes `ai.loader.enabled` (boolean) and `ai.loader.maxLines` (0 = load all) from `application.yml` so you can disable startup ingestion or limit the number of loaded policies for faster iteration.

End of AGENTS guidance.

