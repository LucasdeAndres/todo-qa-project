# To-Do QA Project

![CI](https://github.com/LucasdeAndres/todo-qa-project/actions/workflows/ci.yml/badge.svg)

**A QA Automation portfolio project demonstrating end-to-end testing practice: API testing, UI testing, and CI/CD — built by a QA Manual & Automation engineer, not by a developer trying to write tests as an afterthought.**

The application itself (a To-Do/Kanban manager) is intentionally simple. It exists to be tested, not to impress on its own. This is the first of a 5-project portfolio, each one built specifically to grow a broader QA skill set: manual testing, API automation, UI automation, and CI/CD.

---

## What this project demonstrates

- **Test strategy design**: two independent, isolated test suites (API and UI) running through separate Maven plugins and lifecycle phases, so they can't interfere with each other.
- **27 automated tests**, all following Arrange-Act-Assert, each creating and tearing down its own data — no test depends on another or on pre-loaded fixtures.
- **Real bugs found through testing, root-caused, and fixed at the source** — not tests bent to match buggy behavior. See [Bugs found during testing](#bugs-found-during-testing) below.
- **Boundary and negative testing**: field-length limits, invalid enum values, malformed dates, wrong `Content-Type` headers, non-existent IDs.
- **UI automation with Page Object Model**, running headless, with the three most common real-world Selenium failure modes diagnosed from actual stack traces and fixed with standard techniques (explicit waits, unique test data, robust click handling).
- **CI/CD pipeline** running the full test suite against a real PostgreSQL container on every push — not mocked, not skipped.
- **Test isolation discipline**: every UI test cleans up the data it creates; every API test is independent of execution order.

## Table of Contents

- [What this project demonstrates](#what-this-project-demonstrates)
- [Testing Strategy](#testing-strategy)
- [Bugs found during testing](#bugs-found-during-testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Running the Tests](#running-the-tests)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Key Testing Decisions](#key-testing-decisions)
- [Author](#author)

---

## Testing Strategy

API and UI testing are deliberately isolated at the Maven plugin level, so each runs with its own test runner and lifecycle phase — this is a design decision, not a workaround.

| Layer | Tool | Runner | Maven Plugin | Phase | Tests |
|---|---|---|---|---|---|
| REST API | RestAssured | JUnit 5 | Surefire | `test` | 21 |
| Web UI | Selenium (Page Object Model) | TestNG | Failsafe | `integration-test` / `verify` | 5 |
| App context | Spring Boot | JUnit 5 | Surefire | `test` | 1 |

### API test coverage (RestAssured, 21 tests)

All 5 REST endpoints, plus:
- Field validation: required fields, max-length boundaries, invalid enum values.
- Priority field: default value, explicit valid value, invalid value rejection.
- Due-date field: null handling, valid ISO date, malformed date string, wrong `Content-Type` (415).
- Negative-path coverage: requesting a non-existent ID returns 404 on every relevant endpoint.
- "Honest" DELETE verification: the test doesn't just check for a 204 response — it performs a follow-up GET to confirm the resource is actually gone.

### UI test coverage (Selenium + Page Object Model, 5 tests)

Full CRUD flow exercised through a real (headless) browser: list loads, navigation to the creation form, create, edit (including verifying the old value is gone, not just that the new one appears), and delete.

Built with a Page Object Model (`TaskListPage`, `TaskFormPage`) so implementation details (`By`, `findElement`) never leak into the test classes — a UI change only requires updating one file, not every test that touches that element.

## Bugs found during testing

Every bug below was found by writing or running a test, diagnosed from the actual error/stack trace, and fixed in the production code — not by adjusting the test to accept the wrong behavior.

| Bug | How it was found | Root cause | Fix |
|---|---|---|---|
| `POST /tasks` returned `200 OK` instead of `201 Created` | 4 RestAssured tests failed with `Expected <201> but was <200>` | Controller used `ResponseEntity.ok()` instead of `.status(CREATED)` | Corrected HTTP semantics at the controller level |
| `updateTask()` silently dropped `priority` and `dueDate` on edit | Found manually while testing the UI edit flow — no existing test covered field persistence on update | Service method predated two schema migrations and was never updated to copy the new fields | Fixed the service method; added regression tests that verify via a follow-up GET, not just the response body |
| `NullPointerException` inside RestAssured's HTTP layer | First API test run crashed with no clear cause | RestAssured 5.5.7 incompatible with Groovy 5 (brought in transitively by Spring Boot 4.1.0) | Excluded the transitive Groovy 5 dependency and pinned Groovy 4.0.28 |
| `StaleElementReferenceException` on UI delete test | Intermittent Selenium failures after navigation-triggering clicks | Selenium returns control before the browser finishes a redirect | Added explicit `WebDriverWait` conditions after every navigating action |
| `ElementClickInterceptedException` on UI tests | Selenium found the element but couldn't click it | A growing table (from repeated test runs) pushed buttons outside the clickable viewport | Added `scrollIntoView` + `elementToBeClickable` wait + JS click, applied to all UI interactions |
| Silent test-suite gap: Selenium tests never actually ran in CI | `mvn test` reported 22 passing tests — with no error, and no mention of the 5 Selenium tests | Maven Surefire couldn't resolve which provider (JUnit vs TestNG) to use for the Selenium test class, and silently skipped it | Split test execution: Surefire (JUnit) excludes the `selenium` package; Failsafe (TestNG) exclusively owns it |

## CI/CD Pipeline

On every push and pull request to `master`, GitHub Actions:

1. Spins up a real PostgreSQL 16 service container (with health checks — no mocked database).
2. Checks out the code and sets up JDK 21.
3. Verifies Chrome is available on the runner (for headless Selenium — checked explicitly rather than assumed).
4. Runs `mvn clean verify`, executing both suites: RestAssured (Surefire, `test` phase) and Selenium headless (Failsafe, `integration-test`/`verify` phase).

All Flyway migrations are applied against a clean Postgres instance on every run, so the pipeline also validates the full migration history end-to-end — not just "does it work on my machine."

## Tech Stack

**Backend:** Java 21 · Spring Boot 4.1.0 · Spring Data JPA · Hibernate 7.4.1 · PostgreSQL 16 · Flyway · Jakarta Bean Validation

**Frontend:** Thymeleaf + Bootstrap (server-rendered — required so Selenium has real HTML to test against)

**Testing & Automation:** RestAssured 5.5.7 · JUnit 5 · Selenium 4.27.0 · WebDriverManager 5.9.2 · TestNG 7.10.2 · Maven Surefire + Failsafe

**CI/CD:** GitHub Actions with a live PostgreSQL service container and headless Chrome

## Getting Started

### Prerequisites

- Java 21 (Temurin recommended)
- Maven
- Docker (for PostgreSQL)

### Setup

```bash
git clone https://github.com/LucasdeAndres/todo-qa-project.git
cd todo-qa-project/todo-qa-project

# Start PostgreSQL
docker-compose up -d

# Run the application
mvn spring-boot:run
```

- Web UI: `http://localhost:8080/ui/tasks`
- REST API: `http://localhost:8080/tasks`

## Running the Tests

```bash
# Run everything (API tests + UI tests) — same command used in CI
mvn clean verify

# Run only the API suite (RestAssured / JUnit / Surefire)
mvn clean test

# Run only the UI suite (Selenium / TestNG / Failsafe)
mvn clean verify -DskipTests -DskipITs=false
```

UI tests run in headless Chrome (`--headless=new`) by default — the same command works identically on a local machine and on a CI runner with no display.

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
│   ├── TaskControllerRestAssuredTest.java   # 21 tests — Surefire / JUnit 5
│   ├── pages/                               # Selenium Page Objects
│   │   ├── TaskListPage.java
│   │   └── TaskFormPage.java
│   └── selenium/
│       └── TaskListSeleniumTest.java        # 5 tests — Failsafe / TestNG
└── .github/workflows/ci.yml
```

## Key Testing Decisions

- **Every test is independent.** Each test creates the data it needs via POST/UI action and (for UI tests) tears it down afterward in `@AfterMethod`. No test assumes a particular execution order or pre-existing data — required for a suite that has to pass identically on a clean CI database every time.
- **Surefire/Failsafe split over a single mixed execution.** JUnit 5 and TestNG can't reliably share one Maven test execution; keeping them on separate plugins and lifecycle phases makes the failure mode explicit (a misconfigured suite fails loudly) instead of silent (tests quietly not running, as happened during development — see [Bugs found](#bugs-found-during-testing)).
- **Verify persisted state independently, not just the immediate response.** DELETE tests follow up with a GET; UI edit tests confirm the old value is gone, not just that a new one exists. A response saying "success" and data actually being correct in the database are two different claims — only one of them is what matters.
- **Fix the product, not the test.** Every bug in this repo's history was fixed in `main`, not by loosening an assertion.
- **Headless from the start of CI integration**, with explicit ChromeOptions (`--no-sandbox`, `--disable-dev-shm-usage`, `--disable-gpu`, fixed `--window-size`) rather than relying on defaults that behave differently across environments.

## Author

**Lucas** — QA Manual & Automation Engineer in training
- Background in frontend web development (React) and web programming instruction
- Building a 5-project QA portfolio covering manual testing, API automation, UI automation, and CI/CD, with each project adding a new layer of testing discipline

