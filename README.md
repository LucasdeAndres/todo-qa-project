# To-Do QA Project

![CI](https://github.com/LucasdeAndres/todo-qa-project/actions/workflows/ci.yml/badge.svg)

A full-stack To-Do / Kanban task manager built as a **QA portfolio project**, designed to showcase end-to-end testing practices: API testing, UI testing, and continuous integration — not just application code.

This is the first project in a 5-project QA portfolio roadmap (To-Do → Library Management → Reservation System → Mini E-commerce → Pokémon Version Picker), each one built specifically to practice a growing set of manual and automated testing skills.

---

## Table of Contents

- [Why this project exists](#why-this-project-exists)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Testing Strategy](#testing-strategy)
- [CI/CD Pipeline](#cicd-pipeline)
- [Getting Started](#getting-started)
- [Running the Tests](#running-the-tests)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Key Engineering Decisions](#key-engineering-decisions)
- [Author](#author)

---

## Why this project exists

This isn't primarily a demo of application development — it's a demo of **testing craftsmanship**. The application itself (a simple task manager) is intentionally straightforward; the real focus is:

- A REST API with a full **RestAssured** test suite (validation, boundary cases, HTTP status codes).
- A server-rendered UI with a full **Selenium** test suite using the **Page Object Model**.
- A **CI/CD pipeline** that runs both suites automatically, against a real PostgreSQL instance, on every push.

## Tech Stack

**Backend**
- Java 21
- Spring Boot 4.1.0 (Spring Web, Spring Data JPA)
- Hibernate 7.4.1
- PostgreSQL 16 (Dockerized)
- Flyway (database migrations)
- Bean Validation (Jakarta Validation)

**Frontend**
- Thymeleaf + Bootstrap (server-side rendered UI)

**Testing & Automation**
- RestAssured 5.5.7 — API testing
- Selenium 4.27.0 + WebDriverManager 5.9.2 — UI testing (Page Object Model)
- TestNG 7.10.2 — test runner for Selenium
- JUnit 5 — test runner for RestAssured
- Maven Surefire — runs unit/API tests
- Maven Failsafe — runs UI/integration tests

**CI/CD**
- GitHub Actions, with a real PostgreSQL service container and headless Chrome

## Features

- Create, read, update, and delete tasks
- Task fields: title, description, status, priority, due date
- Field validation with structured error responses
- Full CRUD available both via REST API and via web UI

## Testing Strategy

This project follows a deliberate separation between **API testing** and **UI testing**, each with its own tool, runner, and Maven lifecycle phase:

| Layer | Tool | Runner | Maven Plugin | Phase |
|---|---|---|---|---|
| REST API | RestAssured | JUnit 5 | Surefire | `test` |
| Web UI | Selenium (POM) | TestNG | Failsafe | `integration-test` / `verify` |

**API tests (21 tests):** cover all 5 REST endpoints, field validation, priority and due-date logic (including invalid date formats and incorrect `Content-Type` handling). Every test follows the Arrange-Act-Assert pattern and creates its own data via POST, so the suite can run in any order against an empty database.

**UI tests (5 tests):** cover the full CRUD flow through the browser — list loads, navigation to the creation form, create, edit, and delete — using a Page Object Model (`TaskListPage`, `TaskFormPage`) that hides Selenium implementation details from the test classes. Tests run headless (`--headless=new`) so they work identically on a local machine and on a CI runner with no display.

Real bugs found and fixed along the way (not just tests adjusted to match buggy behavior):
- A POST endpoint incorrectly returned `200 OK` instead of `201 Created` — fixed in the controller.
- `updateTask()` silently failed to persist `priority` and `dueDate` — fixed in the service layer, with new regression tests added.
- `StaleElementReferenceException` in Selenium caused by page redirects — fixed with explicit `WebDriverWait` conditions.
- Test data collisions across UI test runs — fixed by generating unique titles per run.
- Intermittent `ElementClickInterceptedException` from a growing table pushing buttons out of the clickable area — fixed with a `scrollIntoView` + JavaScript click helper applied across all UI interactions.

## CI/CD Pipeline

On every push and pull request to `master`, GitHub Actions:

1. Spins up a real PostgreSQL 16 service container (with health checks).
2. Checks out the code and sets up JDK 21.
3. Verifies Chrome is available on the runner (for headless Selenium).
4. Runs `mvn clean verify`, which executes:
   - The full RestAssured suite (Surefire, `test` phase).
   - The full Selenium suite headless (Failsafe, `integration-test`/`verify` phase).

All database migrations (Flyway) are applied against a clean Postgres instance on every run, so the pipeline also validates the migration history end-to-end.

## Getting Started

### Prerequisites

- Java 21 (Temurin recommended)
- Maven
- Docker (for PostgreSQL)

### Setup

```bash
# Clone the repository
git clone https://github.com/LucasdeAndres/todo-qa-project.git
cd todo-qa-project/todo-qa-project

# Start PostgreSQL
docker-compose up -d

# Run the application
mvn spring-boot:run
```

The application will be available at:
- Web UI: `http://localhost:8080/ui/tasks`
- REST API: `http://localhost:8080/tasks`

## Running the Tests

```bash
# Run everything (API tests + UI tests)
mvn clean verify

# Run only the API test suite (RestAssured / JUnit)
mvn clean test

# Run only the UI test suite (Selenium / TestNG)
mvn clean verify -DskipTests -DskipITs=false
```

> UI tests run in headless Chrome by default, so no visible browser window is required — the same command works locally and in CI.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/tasks` | List all tasks |
| GET | `/tasks/{id}` | Get a single task by ID |
| POST | `/tasks` | Create a new task |
| PUT | `/tasks/{id}` | Update an existing task |
| DELETE | `/tasks/{id}` | Delete a task |

## Project Structure

```
todo-qa-project/
├── src/main/java/com/todoqa/todoqaproject/
│   ├── Task.java                    # Entity with Bean Validation
│   ├── TaskController.java          # REST endpoints
│   ├── TaskViewController.java      # UI endpoints (/ui/tasks)
│   ├── TaskService.java
│   ├── TaskRepository.java
│   └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── templates/                   # Thymeleaf views (list.html, form.html)
│   └── db/migration/                # Flyway migrations (V1, V2, V3)
├── src/test/java/com/todoqa/todoqaproject/
│   ├── TaskControllerRestAssuredTest.java
│   ├── pages/                       # Selenium Page Objects
│   │   ├── TaskListPage.java
│   │   └── TaskFormPage.java
│   └── selenium/
│       └── TaskListSeleniumTest.java
└── .github/workflows/ci.yml
```

## Key Engineering Decisions

- **Thymeleaf over a JS framework for this project:** Selenium tests real rendered HTML, not APIs — a server-rendered UI keeps the focus on testing practice rather than frontend framework overhead. More advanced portfolio projects use React instead.
- **Surefire/Failsafe split:** RestAssured (JUnit 5) and Selenium (TestNG) can't share the same Maven test execution without one interfering with the other. Splitting them by plugin and lifecycle phase keeps each suite isolated and independently runnable.
- **Fix the bug, not the test:** whenever a test failure revealed an actual product defect (e.g. the `updateTask()` persistence bug), the fix went into the production code, with regression tests added — not the other way around.
- **Self-cleaning UI tests:** each Selenium test that creates data removes it in an `@AfterMethod` hook, so the suite doesn't depend on manually resetting the database between runs.

## Author

**Lucas** — QA Manual & Automation Engineer in training
- Background in frontend web development (React) and web programming instruction
- Building a QA portfolio covering manual testing, API testing, UI automation, and CI/CD

