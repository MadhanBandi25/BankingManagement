 # 🏦 Banking Management System · Backend

> A **Spring Boot REST API** for core banking operations — customer management, accounts, transactions (deposit/withdraw/transfer), beneficiaries, loans & loan repayments, and fixed deposits.

---

## 📋 Table of Contents

* [Tech Stack](#-tech-stack)
* [Features](#-features)
* [System Design](#-system-design)
* [Project Structure](#-project-structure)
* [Entity Relationship Overview](#-entity-relationship-overview)
* [API Endpoints](#-api-endpoints)
* [Setup & Installation](#-setup--installation)
* [Environment Configuration](#-environment-configuration)
* [Running the Application](#-running-the-application)
* [Error Handling](#-error-handling)
* [Security Note](#-security-note)

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Mapping | ModelMapper |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Dev Tools | Lombok, Spring DevTools |

> ⚠️ No Spring Security / JWT module is present — all endpoints are currently **unauthenticated**. See [Security Note](#-security-note).

---

## ✨ Features

* **Customer Management** – Register, view, update, delete customers (KYC fields: PAN, Aadhar, DOB, address)
* **Account Management** – Open accounts (Savings/Current/Salary) with initial deposit, view balance, change account status, list by customer/status
* **Transactions** – Deposit, withdraw, and transfer funds between accounts, view transaction history by account or reference number
* **Beneficiaries** – Add beneficiaries for fund transfer, approve/reject, list and delete
* **Loans** – Apply for loans (Home/Personal/Education/Vehicle/Business), approve/reject, view by customer/account/loan ID
* **Loan Payments (EMI)** – Make loan repayments, view payment history by loan/account/reference
* **Fixed Deposits** – Open FDs with tenure & amount, view by account, close FD

---

## 🏗 System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                CLIENT (Browser / Postman)                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │  HTTP/REST (JSON)
                            ▼
┌───────────────────────────────────────────────────────────────────┐
│                   SPRING BOOT APPLICATION                         │
│                   localhost:8080                                  │
│                                                                   │
│  ┌────────────────────┐        ┌────────────────────┐             │
│  │   Controllers      │ ─────▶ │      Services      │            │
│  │ /api/customers     │        │   Business Logic   │             │
│  │ /api/accounts      │        │  (IMPL package)    │             │
│  │ /api/transactions  │        └────────┬───────────┘             │
│  │ /api/beneficiaries │                 │                         │
│  │ /api/loans         │        ┌────────▼───────────┐             │
│  │ /api/loan-payments │        │    Repositories    │             │
│  │ /api/fixed-deposits│        │   (Spring Data)    │             │
│  └────────────────────┘        └────────┬───────────┘             │
│                                         │                         │
└─────────────────────────────────────────┼─────────────────────────┘
                                          │
                        ┌─────────────────▼──────────────────────┐
                        │           MySQL Database               │
                        │                                        │
                        │ ┌──────────┐ ┌──────────┐ ┌───────────┐│
                        │ │customers │ │ accounts │ │transactns ││
                        │ └──────────┘ └──────────┘ └───────────┘│
                        │ ┌──────────────┐ ┌──────┐ ┌───────────┐│
                        │ │beneficiaries │ │loans │ │loan_pmts  ││
                        │ └──────────────┘ └──────┘ └───────────┘│
                        │             ┌─────────────────┐        │
                        │             │ fixed_deposits  │        │
                        │             └─────────────────┘        │
                        └────────────────────────────────────────┘
```

### Transaction Flow

```
DEPOSIT                  WITHDRAW                  TRANSFER
─────────────────────   ─────────────────────   ─────────────────────────
POST /transactions/      POST /transactions/      POST /transactions/
   deposit                  withdraw                  transfer
      │                        │                          │
      ▼                        ▼                          ▼
Account balance ↑        Validate balance            Debit fromAccount
Create TRANSACTION         Account balance ↓          Credit toAccount
record (DEPOSIT)         Create TRANSACTION           Create 2 records:
                          record (WITHDRAWAL)          TRANSFER_DEBIT &
                                                        TRANSFER_CREDIT
```

### Loan & Loan Payment Flow

```
APPLY LOAN              APPROVE/REJECT              MAKE PAYMENT (EMI)
───────────             ──────────────              ───────────────────
POST /api/loans   →    PUT /api/loans/{id}/approve  →  POST /api/loan-payments
status: APPLIED          PUT /api/loans/{id}/reject       │
                          status: APPROVED/REJECTED       ▼
                                                     Deduct EMI from account
                                                     Create LoanPayment record
                                                     with reference number
```

---

## 📁 Project Structure

```
banking-system/
│
├── src/main/java/com/banking/
│   │
│   ├── controller/                      # REST API Controllers
│   │   ├── CustomerController.java      # /api/customers/**
│   │   ├── AccountController.java       # /api/accounts/**
│   │   ├── TransactionController.java   # /api/transactions/**
│   │   ├── BeneficiaryController.java   # /api/beneficiaries/**
│   │   ├── LoanController.java          # /api/loans/**
│   │   ├── LoanPaymentController.java   # /api/loan-payments/**
│   │   └── FixedDepositController.java  # /api/fixed-deposits/**
│   │
│   ├── service/                         # Service interfaces
│   │   ├── CustomerService.java
│   │   ├── AccountService.java
│   │   ├── TransactionService.java
│   │   ├── BeneficiaryService.java
│   │   ├── LoanService.java
│   │   ├── LoanPaymentService.java
│   │   └── FixedDepositService.java
│   │
│   ├── service/IMPL/                    # Service implementations
│   │   ├── CustomerServiceImpl.java
│   │   ├── AccountServiceImpl.java
│   │   ├── TransactionServiceImpl.java
│   │   ├── BeneficiaryServiceImpl.java
│   │   ├── LoanServiceImpl.java
│   │   ├── LoanPaymentServiceImpl.java
│   │   └── FixedDepositServiceImpl.java
│   │
│   ├── entity/                          # JPA Entities & Enums
│   │   ├── Customer.java
│   │   ├── Account.java
│   │   ├── AccountType.java             # SAVINGS, CURRENT, SALARY
│   │   ├── AccountStatus.java           # ACTIVE, BLOCKED, CLOSED
│   │   ├── Transaction.java
│   │   ├── TransactionType.java         # DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT, TRANSFER_CREDIT
│   │   ├── Beneficiary.java
│   │   ├── BeneficiaryStatus.java       # PENDING, APPROVED, REJECTED
│   │   ├── Loan.java
│   │   ├── LoanType.java                # HOME, PERSONAL, EDUCATION, VEHICLE, BUSINESS
│   │   ├── LoanStatus.java              # APPLIED, APPROVED, REJECTED, CLOSED
│   │   ├── LoanPayment.java
│   │   ├── FixedDeposit.java
│   │   └── FixedDepositStatus.java      # ACTIVE, MATURED, CLOSED, PREMATURE_CLOSED
│   │
│   ├── repository/                      # Spring Data JPA Repositories
│   │   ├── CustomerRepository.java
│   │   ├── AccountRepository.java
│   │   ├── TransactionRepository.java
│   │   ├── BeneficiaryRepository.java
│   │   ├── LoanRepository.java
│   │   ├── LoanPaymentRepository.java
│   │   └── FixedDepositRepository.java
│   │
│   ├── dto/
│   │   ├── request/                     # Incoming request bodies
│   │   │   ├── CustomerRequest.java
│   │   │   ├── AccountRequest.java
│   │   │   ├── DepositRequest.java
│   │   │   ├── WithdrawalRequest.java
│   │   │   ├── TransferRequest.java
│   │   │   ├── BeneficiaryRequest.java
│   │   │   ├── LoanRequest.java
│   │   │   ├── LoanPaymentRequest.java
│   │   │   └── FixedDepositRequest.java
│   │   │
│   │   └── response/                    # Outgoing response bodies
│   │       ├── CustomerResponse.java
│   │       ├── AccountResponse.java
│   │       ├── TransactionResponse.java
│   │       ├── BeneficiaryResponse.java
│   │       ├── LoanResponse.java
│   │       ├── LoanPaymentResponse.java
│   │       └── FixedDepositResponse.java
│   │
│   ├── exception/                       # Custom Exceptions
│   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   │   ├── ErrorResponse.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── InsufficientBalanceException.java
│   │   └── InvalidTransactionException.java
│   │
│   └── BankingSystemApplication.java    # Main entry point
│
├── src/main/resources/
│   └── application.properties           # DB config
│
├── src/test/java/
│   └── (test classes here)
│
└── pom.xml                              # Maven dependencies
```

---

## 🗃 Entity Relationship Overview

```
┌──────────────────────────┐
│         CUSTOMER         │
├──────────────────────────┤
│ id (PK)                  │
│ firstName, lastName      │
│ email, phone             │
│ dateOfBirth, address     │
│ panCard, aadharCard      │
└──────────┬───────────────┘
           │ 1
           │ owns
           │ N
┌──────────▼─────────────────────┐
│          ACCOUNT               │
├────────────────────────────────┤
│ id (PK)                        │
│ accountNumber  UNIQUE          │
│ customer_id    FK → customer   │
│ accountType    ENUM            │◄── SAVINGS | CURRENT | SALARY
│ status         ENUM            │◄── ACTIVE | BLOCKED | CLOSED
│ balance        DECIMAL         │
│ branch, ifscCode               │
└─────┬───────┬────────────┬─────┘
      │ N     │ N          │ N
      │       │            │
┌─────▼───┐ ┌─▼─────────┐ ┌▼────────────────┐
│TRANSACTN│ │BENEFICIARY│ │     LOAN        │
├─────────┤ ├─────────-─┤ ├─────────────────┤
│type ENUM│ │status ENUM│ │loanType ENUM    │
│amount   │ │(PENDING/  │ │status ENUM      │
│refNumber│ │ APPROVED/ │ │(APPLIED/        │
│         │ │ REJECTED) │ │ APPROVED/       │
└─────────┘ └───────────┘ │ REJECTED/CLOSED)│
                          └────────┬────────┘
                                   │ 1
                                   │ N
                            ┌──────▼──────────┐
                            │  LOAN_PAYMENT   │
                            ├─────────────────┤
                            │ loan_id FK      │
                            │ account_id FK   │
                            │ referenceNumber │
                            └─────────────────┘

┌──────────────────────────────┐
│       FIXED_DEPOSIT          │
├──────────────────────────────┤
│ account_id FK → account      │
│ depositAmount  DECIMAL       │
│ tenureMonths   INT           │
│ status ENUM (ACTIVE/MATURED/ │
│   CLOSED/PREMATURE_CLOSED)   │
└──────────────────────────────┘
```

---

## 🔌 API Endpoints

> Base URL: `http://localhost:8080`

### 👤 Customers (`/api/customers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/customers` | Create a new customer |
| GET | `/api/customers/{id}` | Get customer by ID |
| GET | `/api/customers` | Get all customers |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

**Example – Create Customer**
```http
POST /api/customers
Content-Type: application/json

{
  "firstName": "Madhan",
  "lastName": "Kumar",
  "email": "madhan@example.com",
  "phone": "9876543210",
  "dateOfBirth": "1998-05-10",
  "address": "12 Main Street, Bengaluru",
  "panCard": "ABCDE1234F",
  "aadharCard": "123456789012"
}
```

---

### 🏦 Accounts (`/api/accounts`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts` | Open a new account |
| GET | `/api/accounts/customer/{customerId}` | Get accounts by customer |
| GET | `/api/accounts` | Get all accounts |
| GET | `/api/accounts/{accountNumber}` | Get account by account number |
| GET | `/api/accounts/{accountNumber}/balance` | Get account balance |
| GET | `/api/accounts/status/{status}` | Get accounts by status |
| PUT | `/api/accounts/{accountNumber}/status` | Update account status |

**Example – Open Account**
```http
POST /api/accounts
Content-Type: application/json

{
  "customerId": 1,
  "accountType": "SAVINGS",
  "initialDeposit": 5000,
  "branch": "MG Road Branch",
  "ifscCode": "SBIN0001234"
}
```

**Example – Update Account Status**
```http
PUT /api/accounts/SB1000123456/status?status=BLOCKED
```

---

### 💸 Transactions (`/api/transactions`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/deposit` | Deposit money into an account |
| POST | `/api/transactions/withdraw` | Withdraw money from an account |
| POST | `/api/transactions/transfer` | Transfer money between accounts |
| GET | `/api/transactions/account/{accountNumber}` | Get transaction history for an account |
| GET | `/api/transactions/reference/{referenceNumber}` | Get transaction by reference number |

**Example – Deposit**
```http
POST /api/transactions/deposit
Content-Type: application/json

{
  "accountNumber": "SB1000123456",
  "amount": 2000,
  "description": "Cash deposit"
}
```

**Example – Withdraw**
```http
POST /api/transactions/withdraw
Content-Type: application/json

{
  "accountNumber": "SB1000123456",
  "amount": 500,
  "description": "ATM withdrawal"
}
```

**Example – Transfer**
```http
POST /api/transactions/transfer
Content-Type: application/json

{
  "fromAccountNumber": "SB1000123456",
  "toAccountNumber": "SB1000654321",
  "amount": 1000,
  "description": "Rent payment"
}
```

---

### 👥 Beneficiaries (`/api/beneficiaries`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/beneficiaries` | Add a beneficiary |
| PUT | `/api/beneficiaries/{id}/approve` | Approve beneficiary |
| PUT | `/api/beneficiaries/{id}/reject` | Reject beneficiary |
| GET | `/api/beneficiaries/{id}` | Get beneficiary by ID |
| GET | `/api/beneficiaries/account/{accountNumber}` | Get beneficiaries for an account |
| GET | `/api/beneficiaries` | Get all beneficiaries |
| DELETE | `/api/beneficiaries/{id}` | Delete beneficiary |

**Example – Add Beneficiary**
```http
POST /api/beneficiaries
Content-Type: application/json

{
  "accountNumber": "SB1000123456",
  "beneficiaryName": "Ravi Shankar",
  "beneficiaryAccountNumber": "HD2000998877",
  "ifscCode": "HDFC0001234",
  "bankName": "HDFC Bank"
}
```

---

### 📑 Loans (`/api/loans`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/loans` | Apply for a loan |
| PUT | `/api/loans/{loanId}/approve` | Approve a loan |
| PUT | `/api/loans/{loanId}/reject` | Reject a loan |
| GET | `/api/loans/customer/{customerId}` | Get loans by customer |
| GET | `/api/loans/{loanId}` | Get loan by ID |
| GET | `/api/loans/account/{accountNumber}` | Get loans by account |
| GET | `/api/loans` | Get all loans |

**Example – Apply for Loan**
```http
POST /api/loans
Content-Type: application/json

{
  "customerId": 1,
  "accountNumber": "SB1000123456",
  "loanType": "PERSONAL",
  "loanAmount": 50000,
  "tenureMonths": 12,
  "reason": "Home renovation"
}
```

---

### 💵 Loan Payments (`/api/loan-payments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/loan-payments` | Make a loan repayment (EMI) |
| GET | `/api/loan-payments/loan/{loanId}` | Get payments by loan |
| GET | `/api/loan-payments/account/{accountNumber}` | Get payments by account |
| GET | `/api/loan-payments/{id}` | Get payment by ID |
| GET | `/api/loan-payments/reference/{referenceNumber}` | Get payment by reference number |

**Example – Make Loan Payment**
```http
POST /api/loan-payments
Content-Type: application/json

{
  "loanId": 1,
  "accountNumber": "SB1000123456"
}
```

---

### 📈 Fixed Deposits (`/api/fixed-deposits`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/fixed-deposits` | Open a fixed deposit |
| GET | `/api/fixed-deposits/{fdId}` | Get FD by ID |
| GET | `/api/fixed-deposits/account/{accountNumber}` | Get FDs by account |
| GET | `/api/fixed-deposits` | Get all FDs |
| PUT | `/api/fixed-deposits/{fdId}/closed` | Close a fixed deposit |

**Example – Open Fixed Deposit**
```http
POST /api/fixed-deposits
Content-Type: application/json

{
  "accountNumber": "SB1000123456",
  "depositAmount": 10000,
  "tenureMonths": 12
}
```

---

## ⚙️ Setup & Installation

### Prerequisites

```
✅ Java 17+
✅ Maven 3.8+
✅ MySQL 8.0+
```

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/BankingManagement.git
cd BankingManagement/banking-system
```

### Step 2: Create MySQL Database

```sql
CREATE DATABASE banking_management;
```

### Step 3: Configure `application.properties`

```properties
spring.application.name=banking-system

spring.datasource.url=jdbc:mysql://localhost:3306/banking_management
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## 🌐 Environment Configuration

| Property | Description | Must Change Before Pushing |
|----------|-------------|------------------------------|
| `spring.datasource.username` / `password` | MySQL credentials | ✅ |
| `spring.datasource.url` | DB host/port/schema name | If deploying elsewhere |

---

## ▶ Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## ❌ Error Handling

All errors follow this standard format (via `GlobalExceptionHandler`):

```json
{
  "timestamp": "2026-06-13T12:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Account not found with number: SB1000123456",
  "path": "/api/accounts/SB1000123456"
}
```

| Exception | HTTP Status | Error Code |
|-----------|-------------|------------|
| ResourceNotFoundException | 404 | NOT_FOUND |
| InsufficientBalanceException | 400 | INSUFFICIENT_BALANCE |
| InvalidTransactionException | 400 | INVALID_TRANSACTION |
| MethodArgumentNotValidException | 400 | VALIDATION_ERROR |
| Exception (generic) | 500 | INTERNAL_SERVER_ERROR |

---

## ⚠️ Security Note

This project currently has **no authentication/authorization layer** (no Spring Security/JWT) — all endpoints are publicly accessible. Before deploying or pushing publicly:

1. Add Spring Security with JWT-based authentication and role-based access control (e.g., CUSTOMER, TELLER, ADMIN roles), similar to other projects in this portfolio.
2. Move `spring.datasource.password` to an environment variable.
3. Add input validation, sanitization, and rate limiting on transaction endpoints (deposit/withdraw/transfer) to prevent abuse.
4. Add `.gitignore` for `application.properties` and provide an `application.properties.example` template.

---
