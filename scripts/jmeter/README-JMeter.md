JMeter Performance Test Guide for Local LLM RAG Application

Overview
--------
This folder contains a JMeter test plan (`test-plan.jmx`), a small PowerShell generator to create sample questions (`generate-questions.ps1`), and a PowerShell runner (`run-tests.ps1`) that runs the plan for multiple user counts and produces JMeter HTML dashboard reports.

Prerequisites
-------------
- Java 8+ installed
- Apache JMeter (recommended 5.5+) installed and `jmeter` on PATH (or update `run-tests.ps1` to point to `jmeter.bat`)
- Application under test running locally at http://localhost:8080 (start with `mvn spring-boot:run` in project root)
- From PowerShell, you can run the scripts in this folder.

Files
-----
- `test-plan.jmx`: JMeter test plan. Uses CSV Data Set Config for the `question` values.
- `generate-questions.ps1`: Generates a CSV file of sample questions (default 1000 rows).
- `run-tests.ps1`: Executes the JMeter test plan for multiple user counts and generates HTML reports.

Quick Start (PowerShell)
------------------------
1. Start the Spring Boot app:

```powershell
cd C:\Users\Admin\Downloads\springboot-local-llm-rag-poc\springboot-local-llm-rag-poc
mvn spring-boot:run
```

2. In a new PowerShell window, prepare test data and run tests:

```powershell
cd C:\Users\Admin\Downloads\springboot-local-llm-rag-poc\springboot-local-llm-rag-poc\scripts\jmeter
# generate 1000 questions
.\generate-questions.ps1 -Count 1000 -OutFile questions.csv

# run tests for the default user counts (5,10,50,100,500,1000)
.\run-tests.ps1 -UserCounts @(5,10,50,100,500,1000) -Iterations 1 -Ramp 5
```

Notes & Recommendations
-----------------------
- If you don't have a large CSV, JMeter will recycle rows by default. Use unique rows if you need distinct requests per virtual user.
- Adjust `-Iterations` in `run-tests.ps1` to control how many POST requests each thread makes. If you want sustained load, increase Iterations or use the ThreadGroup scheduler.
- For large user counts (500/1000), run the test from a machine with enough CPU/RAM and ensure the Spring Boot app has sufficient heap and thread limits.
- Use the generated HTML reports in `scripts/jmeter/results/report_<threads>` to inspect throughput, response times, and errors.

Interpreting Results
--------------------
Key metrics in the dashboard and in the `.jtl`:
- Throughput (requests/sec): how many requests your app processes per second under load.
- Average / Median / 90% / 95% / 99% response times: latency distribution.
- Error % and response codes: indicates correctness under load.
- Active Threads: concurrency over time.

If you run the tests and paste the produced `results/report_<threads>/index.html` summary here or the `results/results_<threads>.jtl` files, I can help analyze the outcomes and recommend tuning changes.

Limitations
-----------
I cannot run JMeter on your machine from this environment. The scripts and test plan are provided so you can run them locally and collect the real execution results. If you'd like, you can upload the generated `.jtl` or HTML report and I will analyze them and produce a detailed performance assessment.

