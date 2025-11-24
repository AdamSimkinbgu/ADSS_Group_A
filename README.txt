ADSS – Advanced Distributed Software System
===========================================

Overview
--------
This project implements a multi-module distributed system developed for the
"Super-Li" food chain as part of a university Software Engineering course.
The system replaces the chain’s manual processes with digital management
tools. It includes four main modules:
1. Employee Management
2. Inventory Management
3. Supplier Management
4. Logistics and Deliveries

My Contribution – Employee Module
---------------------------------
I was responsible for designing and implementing the Employee Management Module.
This included:

- Employee data model (personal info, bank details, employment terms)
- Role and permission system (cashier, shift manager, warehouse worker, etc.)
- Employee availability constraints for scheduling
- Shift assignment logic matching availability and role requirements
- Validation rules ensuring roles match tasks
- Service-level business logic and domain relationships

Technologies
------------
- Java
- Spring Boot
- Maven
- REST Architecture
- Layered Design (Domain → Service → Controller)
- OOP Principles

Project Structure
-----------------
domain/        - Business entities, validations, relationships
service/       - Core logic, workflows, business rules
controller/    - REST endpoints (if included in the project)
utils/         - Shared utilities

System Modules (High-Level)
---------------------------
Employee Module     – Employee records, roles, permissions, shifts, availability
Inventory Module    – Item tracking, locations, thresholds, defects, categories
Supplier Module     – Supplier contracts, pricing, catalog mapping, delivery terms
Logistics Module    – Trucks, deliveries, routes, weights, constraints

Documentation
-------------
- Based on the full system specification provided in the course
- Includes use-case modeling, design principles, and architecture diagrams


Duo A - Suppliers
Adam Simkin      206001018 simkinad@post.bgu.ac.il
Doron Barski     313351439 barskid@post.bgu.ac.il

Duo C - Inventory
Moshe Klein      206641649 moshekl@post.bgu.ac.il
Ben Zion Hadad   315057323 Bentziha@post.bgu.ac.il
