# ADSS – Advanced Distributed Software System

This repository contains a multi-module distributed information system developed
as part of the *Advanced Distributed Software Systems* course at Ben-Gurion University.
The project models the full digital transformation of the “Super-Li” retail chain,
transitioning from manual, paper-based workflows to a unified, scalable software platform.

---

## 🚀 Project Overview

The system is built around four major subsystems, each representing a key operational
area within the retail chain:

### 1. Employee Management
Handles employee records, roles, permissions, availability constraints, and shift assignments.
Ensures correct matching between employee roles and required tasks.

### 2. Inventory Management
Tracks item quantities, locations (store floor vs. warehouse), product categories,
defective items, expiration, threshold alerts, and pricing details including promotions.

### 3. Supplier Management
Manages supplier profiles, delivery schedules, agreements, catalog mappings,
payment terms, quantity-based discounts, and purchase order data.

### 4. Logistics & Deliveries
Handles trucks, routes, delivery scheduling, destination lists, weight constraints,
driver licenses, and multi-stop delivery operations.

---

## 🧩 System Architecture

The project follows a **layered architectural design** for each subsystem:

- **Domain Layer** – Core business entities, relationships, and validation rules  
- **Service Layer** – Business logic, workflows, and inter-module coordination  
- **Controller Layer (optional)** – REST endpoints (if applicable to the specific module)  
- **Utilities** – Shared helpers and infrastructure components  

This structure promotes modularity, maintainability, and testability.

---

## 📚 Documentation & Design

The development process was guided by the full system specification provided in the course,  
including:

- Use cases  
- Domain models  
- Sequence and activity diagrams  
- Subsystem interactions  
- Business rules and constraints  

Each module was designed based on real-world retail operations, with emphasis on data consistency
and integration across all parts of the system.

---

## 🛠️ Technologies

- Java  
- Spring Boot  
- Maven  
- REST architectural concepts  
- Object-Oriented Design  
- Layered system structure  

---

## 📂 Project Structure (General)
/domain – Business entities and validation logic
/service – Application workflows and business rules
/controller – REST API endpoints (if implemented)
/utils – Shared utility components

Each subsystem follows this structure within its own dedicated package/module.

---

This project was developed as a collaborative group assignment as part of the Software System Analysis and Design course.
All team members contributed to the analysis, design, and implementation of the system.
