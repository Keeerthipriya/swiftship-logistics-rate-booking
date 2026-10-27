# SwiftShip Logistics — Rate Calculation & Parcel Booking Engine

> A SOLID-principle-driven, spec-first capstone project built with **Java 25 / Spring Boot 3.4 / Apache CXF JAX-RS** on the backend and **Angular 19 Standalone Components with Signals** on the frontend.

---

## Table of Contents

1. [Quick Start](#quick-start)
2. [Architecture Overview](#architecture-overview)
3. [Business Rules](#business-rules)
4. [API Contract (OpenAPI 3.1)](#api-contract)
5. [SOLID Principle Mapping](#solid-principle-mapping)
6. [Project Structure](#project-structure)
7. [Running the Tests](#running-the-tests)
8. [Tech Stack](#tech-stack)

---

## Quick Start

### Prerequisites

| Tool          | Minimum Version | Notes                          |
|---------------|-----------------|--------------------------------|
| Java (JDK)    | 25 LTS          | Used via `JAVA_HOME`           |
| Apache Maven  | 3.9+            | Included or via system install |
| Node.js       | 18+             | v26.x recommended              |
| npm           | 9+              |                                |

---

### 1. Start the Backend (Spring Boot + CXF JAX-RS)

```bash
cd backend
mvn spring-boot:run
```

Backend starts on **http://localhost:8080**
- API base path: `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/h2-console`  
  JDBC URL: `jdbc:h2:mem:shippingdb`, User: `sa`, Password: *(empty)*

---

### 2a. Run Angular Dev Server with Proxy (Recommended for development)

```bash
cd frontend
npm install
npm start          # Serves SPA on http://localhost:3000 and proxies /api → :8080
```

Open **http://localhost:3000**

---

### 2b. Build SPA + Run Node.js Production Host

```bash
# Step 1 – build the Angular SPA
cd frontend
npm install
npm run build      # Outputs to frontend/dist/swiftship-frontend/browser

# Step 2 – start the Node.js host + proxy
cd ../server
npm install
npm start          # Serves on http://localhost:3000
```

Open **http://localhost:3000**

---

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────────────┐
│                    Browser  (Angular 19 SPA)                         │
│                                                                      │
│   app.component.ts (Signals orchestrator)                            │
│       ├── ParcelFormComponent     (weight + distance input)          │
│       ├── TierSelectorComponent   (OCP – dynamic tier cards)        │
│       ├── QuoteSummaryComponent   (ISP – quote display model only)   │
│       ├── PaymentPickerComponent  (payment method + reference)       │
│       └── BookingResultComponent  (confirmation receipt)             │
│                                                                      │
│   SHIPPING_API_TOKEN  ──► ShippingApiService (prod) / Mock (test)   │
└──────────────────────┬───────────────────────────────────────────────┘
                       │ HTTP /api/* (proxy)
┌──────────────────────▼───────────────────────────────────────────────┐
│               Node.js Express Server  (:3000)                        │
│    Static SPA + Reverse Proxy: /api → :8080                          │
└──────────────────────┬───────────────────────────────────────────────┘
                       │ HTTP
┌──────────────────────▼───────────────────────────────────────────────┐
│           Apache CXF JAX-RS Server  (:8080/api)                      │
│                                                                      │
│   QuoteResourceImpl  ──► QuoteService  ──► RateCalculationCoordinator│
│   BookingResourceImpl ──► BookingService ──► [QuoteService           │
│                                              PaymentCoordinator      │
│                                              BookingRepository]      │
│                                                                      │
│   Rate Calculators (Strategy Pattern):                               │
│     StandardRateCalculator                                           │
│     ExpressRateCalculator                                            │
│     SameDayRateCalculator  (LSP enforced)                           │
│                                                                      │
│   Payment Processors (Strategy Pattern):                             │
│     CreditCardPaymentProcessor (simulated)                           │
│     CorporateAccountPaymentProcessor (simulated)                     │
│                                                                      │
│   Persistence:  H2 In-Memory (jdbc:h2:mem:shippingdb)               │
│                 Spring Data JPA / Hibernate                          │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Business Rules

| Tier       | Delivery     | Base Fee | Per kg  | Per km  | Max Distance |
|------------|--------------|----------|---------|---------|--------------|
| STANDARD   | 3–5 days     | \$5.00   | \$1.20  | \$0.05  | 2,500 km     |
| EXPRESS    | Next day     | \$15.00  | \$2.50  | \$0.12  | 2,500 km     |
| SAME_DAY   | Within 4 hrs | \$35.00  | \$4.00  | \$0.25  | **100 km**   |

- **Weight**: 0.1 kg – 50.0 kg
- **Distance**: 1 km – 2,500 km
- **Payment**: Credit Card or Corporate Account Billing (simulated)

---

## API Contract

Full OpenAPI 3.1 specification: [`openapi/openapi.yaml`](openapi/openapi.yaml)

| Method | Path              | Description                            |
|--------|-------------------|----------------------------------------|
| POST   | `/api/quotes`     | Calculate shipping quote               |
| GET    | `/api/tiers`      | List tiers with eligibility evaluation |
| POST   | `/api/bookings`   | Create & confirm parcel booking        |
| GET    | `/api/bookings/{id}` | Retrieve booking by ID              |

**Error Responses** use structured JSON:
```json
{
  "errorCode": "TIER_NOT_ELIGIBLE",
  "message": "Same-Day delivery is restricted to distances <= 100 km (requested: 150.0 km)",
  "status": 422,
  "timestamp": "2026-10-26T18:00:00Z",
  "details": ["Tier SAME_DAY is restricted to distances <= 100 km"]
}
```

---

## SOLID Principle Mapping

### SRP – Single Responsibility Principle

| Layer    | Class / Component          | Single Responsibility                              |
|----------|----------------------------|----------------------------------------------------|
| Backend  | `QuoteResourceImpl`        | HTTP binding and JAX-RS Response for quotes        |
| Backend  | `BookingResourceImpl`      | HTTP binding and JAX-RS Response for bookings      |
| Backend  | `BookingServiceImpl`       | Booking workflow orchestration only                |
| Backend  | `RateCalculationCoordinator` | Strategy dispatch + physical limit validation    |
| Backend  | `CxfJaxRsConfig`           | CXF server configuration only                     |
| Frontend | `ParcelFormComponent`      | Captures weight and distance inputs                |
| Frontend | `TierSelectorComponent`    | Renders and selects speed tiers                    |
| Frontend | `QuoteSummaryComponent`    | Displays calculated fee breakdown                  |
| Frontend | `PaymentPickerComponent`   | Payment method selection                           |
| Frontend | `BookingResultComponent`   | Booking confirmation receipt display               |

---

### OCP – Open/Closed Principle

**Backend**: The `RateCalculationCoordinator` auto-discovers all `RateCalculator` Spring beans.  
Adding a new speed tier = implement `RateCalculator` + `@Component`. Zero coordinator edits.  
See `RateTierExtensionSample.java` and `OpenClosedExtensionTest.java` for demonstration.

**Frontend**: `TierSelectorComponent` iterates dynamically over `TierCardView[]`.  
New tiers returned by the API render automatically — no template `*ngIf tier === 'X'` conditionals.

---

### LSP – Liskov Substitution Principle

**Backend**: `SameDayRateCalculator` enforces its documented precondition (≤ 100 km).  
Violating requests throw `TierNotEligibleException` — a **predictable, documented business rejection** — not a `NullPointerException` or corrupt calculation.  
`BusinessExceptionMapper` translates it to a clean `HTTP 422`.  
See `SameDayLiskovSubstitutionTest.java`.

**Frontend**: `MockShippingApiService` faithfully mirrors the same domain rules as `ShippingApiService`.  
Same-Day > 100km throws the same structured error observable — no silent swallowing or unexpected exceptions.

---

### ISP – Interface Segregation Principle

**Backend**:
- `QuoteService` — read-only rate/tier operations
- `BookingService` — booking creation and lookup mutations
- Clients depending only on quotes never see booking methods.

**Frontend**:
- `QuoteSummaryComponent` receives only `QuoteResult` (fees + totals), never booking IDs or payment tokens.
- `TierCardView` is a focused presentational model, segregated from the full `ParcelBooking` entity.
- `ParcelInput`, `ContactDetails`, `PaymentSelection` are purpose-specific interfaces.

---

### DIP – Dependency Inversion Principle

**Backend**:
- `BookingServiceImpl` injects `QuoteService`, `PaymentCoordinator`, `BookingRepository` abstractions.
- `PaymentCoordinator` injects `List<PaymentProcessor>` — never a concrete gateway SDK.
- `RateCalculationCoordinator` injects `List<RateCalculator>` — never specific tier classes.
- All injection is **constructor-based**.
- See `ReplaceableDependencyTest.java` — substituting mock dependencies confirms zero coupling.

**Frontend**:
- `SHIPPING_API_TOKEN` (`InjectionToken<ShippingApiClient>`) is injected into `AppComponent`.
- `app.config.ts` maps the token to `ShippingApiService` (production).
- Swap to `MockShippingApiService` for offline/testing by changing one line in `app.config.ts`.

---

## Project Structure

```
ssd/
├── openapi/
│   └── openapi.yaml                    # OpenAPI 3.1 contract
│
├── backend/                            # Java 25 / Spring Boot / Apache CXF JAX-RS
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/swiftship/logistics/
│       │   ├── SwiftShipApplication.java
│       │   ├── api/                    # JAX-RS interfaces + implementations + exception mappers
│       │   │   ├── QuoteApi.java
│       │   │   ├── BookingApi.java
│       │   │   ├── dto/                # Request/Response DTOs
│       │   │   ├── exception/          # JAX-RS ExceptionMappers (LSP boundary)
│       │   │   └── impl/              # JAX-RS Resource implementations (SRP)
│       │   ├── config/                 # CxfJaxRsConfig + RateTierExtensionSample (OCP demo)
│       │   ├── domain/                 # Enums, JPA Entity, Value Objects, Domain Exceptions
│       │   ├── rate/                   # RateCalculator interface + 3 tier implementations (OCP/LSP)
│       │   ├── payment/                # PaymentProcessor interface + 2 simulated providers (DIP)
│       │   ├── repository/             # Spring Data JPA BookingRepository (DIP)
│       │   └── service/                # QuoteService + BookingService interfaces (ISP) + impls
│       └── src/test/
│           ├── rate/RateCalculationTests.java          # Formula unit tests
│           ├── rate/SameDayLiskovSubstitutionTest.java # LSP demo
│           ├── rate/OpenClosedExtensionTest.java       # OCP demo
│           ├── payment/PaymentProcessorTests.java      # Payment simulation tests
│           └── service/BookingServiceIntegrationTest.java # Spring Boot integration test
│           └── service/ReplaceableDependencyTest.java  # DIP mock substitution test
│
├── frontend/                           # Angular 19 Standalone SPA
│   ├── angular.json
│   ├── proxy.conf.json                 # Dev proxy /api → :8080
│   ├── tsconfig.json
│   ├── package.json
│   └── src/
│       ├── main.ts
│       ├── index.html
│       ├── styles.css
│       └── app/
│           ├── app.component.ts        # Root orchestrator with Signals
│           ├── app.config.ts           # DI token binding (swap prod/mock here)
│           ├── core/
│           │   ├── models/             # Segregated TypeScript interfaces (ISP)
│           │   ├── services/           # ShippingApiClient interface + prod + mock impls
│           │   └── tokens/             # SHIPPING_API_TOKEN InjectionToken (DIP)
│           └── components/             # 5 focused standalone components (SRP)
│               ├── parcel-form/
│               ├── tier-selector/      # OCP – dynamic tier rendering
│               ├── quote-summary/      # ISP – receives only QuoteResult
│               ├── payment-picker/
│               └── booking-result/
│
└── server/                             # Node.js 18+ Express host + reverse proxy
    ├── package.json
    └── server.js
```

---

## Running the Tests

```bash
cd backend
mvn test
```

Test suite includes:
- **Rate formula unit tests** (`RateCalculationTests`) — verifies STANDARD / EXPRESS / SAME_DAY fee calculations
- **LSP rejection tests** (`SameDayLiskovSubstitutionTest`) — confirms predictable 422 rejection for > 100 km Same-Day
- **OCP extension test** (`OpenClosedExtensionTest`) — proves new calculator plug-in without editing coordinator
- **Payment simulation tests** (`PaymentProcessorTests`) — Credit Card and Corporate Account simulated flows
- **Integration test** (`BookingServiceIntegrationTest`) — full Spring Boot context, H2 persistence, rejection scenarios
- **DIP mock substitution test** (`ReplaceableDependencyTest`) — swaps payment infrastructure with Mockito mocks

---

## Tech Stack

| Layer      | Technology                            |
|------------|---------------------------------------|
| Backend    | Java 25 LTS, Spring Boot 3.4          |
| API Layer  | Apache CXF 4.x, JAX-RS (Jakarta EE)  |
| Persistence| Spring Data JPA, H2 In-Memory         |
| Frontend   | Angular 19 Standalone + Signals       |
| Node Host  | Node.js 18+, Express 4.x             |
| API Spec   | OpenAPI 3.1 (YAML)                    |
| Testing    | JUnit 5, AssertJ, Mockito, Spring Boot Test |

---

> **Submission**: This repository is designed as a GitHub Pull Request submission.  
> All tests are green. The application runs as a full vertical slice from browser to H2 database.
