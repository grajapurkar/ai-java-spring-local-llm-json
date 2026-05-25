# DEV: Fast iteration and troubleshooting

This file collects practical tips for developing and debugging this project locally.

1) Fast iteration (dev profile)
- Use the `dev` profile to limit startup ingestion: `src/main/resources/application-dev.yml` (defaults: `ai.loader.enabled=true`, `ai.loader.maxLines=20`).
- Helper script (Windows PowerShell): `./scripts/run-dev.ps1` — builds the project, starts Spring Boot with the `dev` profile, waits for startup markers and posts a sample request.

2) If the app fails on startup
- Missing model/tokenizer: `EmbeddingService.init()` will throw `RuntimeException("model.onnx not found")` or tokenizer not found. Verify `src/main/resources/model/model.onnx` and `tokenizer.json` exist and are packaged by Maven.
- ONNX native library errors: `onnxruntime` relies on native JNI libraries. If you see UnsatisfiedLinkError or similar, ensure the onnxruntime native artifact is available for your platform or run tests with a mocked `EmbeddingService`.

3) Running tests reliably
- The repository includes `ChatControllerIntegrationTest` which overrides `EmbeddingService` with a deterministic mock to avoid ONNX native dependency. Run tests with `mvn test`.
- If you need to run the app but avoid embedding computation entirely, set `ai.loader.enabled=false` in `application.yml` or `application-dev.yml`.

4) How to mock or replace the embedding pipeline
- Quick local mock: edit `EmbeddingService.generateEmbedding` to return a deterministic pseudorandom float[] (a commented-out mock exists at the end of the file).
- Better option for tests: provide a test configuration bean (see `src/test/java/com/example/aipoc/ChatControllerIntegrationTest.java`).

5) Logs and diagnostics
- `EmbeddingService` prints model and tokenizer paths and a "Tokenizer Loaded Successfully" message at startup; use these prints to confirm resource loading.
- `DataLoaderConfig` prints "Policies loaded into vector store" when ingestion completes.

6) Changing retrieval size
- Edit `RagService.retrieve` to change the `topK` passed to `vectorStore.similaritySearch(...)` (currently 3).

7) Rebuilding after resource changes
- Re-run `mvn clean package` to ensure resources are bundled into the JAR.

