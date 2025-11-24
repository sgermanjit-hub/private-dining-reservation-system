# private-dining-reservation-system
Private Dining Reservation System is a spring boot Java application that helps to manage reservations of private rooms for a restaurant.

## Features
### Restaurant Management
- Create, Update and fetch restaurants
- Create and manage rooms belongs to a restaurant with capacity and min spend

### Room Management
- Reserve a specific room
- Auto Assign Room based on RoomType and restaurant
- Prevent Double bookings
- Cancel Reservations

### Availability Service
- Fetch available rooms based on date and reservation time period
- Defaulting to current remaining day for better user experience

### Validations
- Input validations for reservation and restaurant data 

### Error Handling
- Room not available
- Reservation conflicts
- Reservation failure
- Entity not found
- Validation failures
- Global exception handler with consistent Json response

## Tech Stack
- Java 21
- Spring Boot 3
- Maven
- PostgresSQL
- TestContainers
- Lombok
- Docker Desktop

## Start Docker Infrastructure
From root of the project
```bash
docker compose up -d 

## Verify Docker
docker ps (3 containers)
```

## Running Application
```
 git clone git@github.com:sgermanjit-hub/private-dining-reservation-system.git
 
 cd private-dining-reservation-system
 
 mvn clean compile install
 
 mvn spring-boot:run
````
## Testing
- Integration Tests
  - Test environment using Postgres containers
  - Tests cover all main scenarios
    - Create Reservation
    - Auto Assign Reservation Room
    - Reservation conflicts handling
    - Prevents Double Booking
    - List Reservations by Diner and Restaurant
    - Cancellation of reservation
```    
   mvn test
```
- Included double booking test 

    
## Api Documentation
```bash
Swagger UI
👉 http://localhost:8080/swagger-ui/index.html

## Design Docs
👉 https://docs.google.com/document/d/1BMy3sYyWTxThd-kjxHmdJigjEX9KW-jfrcSlioZbPws/edit?tab=t.0

## ADR
👉 Database Design https://docs.google.com/document/d/1mi8ZFily__Qj0MCOf1UbpyBFkrX8oYQt6adxGGlPyS8/edit?tab=t.0#heading=h.abgg4cyfohzo
👉 Concurrency Control https://docs.google.com/document/d/1UM7Ak0sB0-xx4DcMDOFvy_uV5YVM0GU3Vx2wiZ1FU7A/edit?tab=t.0#heading=h.pnn23mp761pq

