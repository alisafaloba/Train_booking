# Train Ticketing API Documentation

A robust Java Spring Boot backend application designed to manage train schedules, routes, and passenger bookings. This API supports complex route finding, strict capacity management, role-based security, and automated email notifications.

## 1. Technical Stack
* **Language:** Java 17+
* **Framework:** Spring Boot (Web, Data JPA, Security, Mail)
* **Database:** H2 (In-Memory Database for rapid testing and evaluation)
* **Authentication:** Spring Security (Basic Auth with BCrypt password encoding)

---

## 2. Architecture & Implementation Logic

The application fulfills the assignment requirements through the following programmatic solutions:

### Requirement A: Booking Engine & Overbooking Prevention
The application ensures that trains are never overbooked by dynamically calculating capacity at the time of the transaction.
* **Solution Logic:** When a booking request is received, the `BookingService` queries the database to find the sum of all currently booked seats on that specific route. It subtracts this sum from the train's total physical capacity.
* **Enforcement:** If the requested seats exceed the available capacity, the transaction is immediately halted, an `IllegalStateException` is thrown, and a `400 Bad Request` is returned to the client. 
* **Notifications:** Upon a successful booking, the `EmailService` uses Spring's `JavaMailSender` to asynchronously dispatch a confirmation email to the user.

### Requirement B: Route Searching Algorithm
The `RouteService` handles journey planning between any two given stations using a two-tier verification approach:
* **Direct Routes:** It first queries the database for any single route that contains both the departure and arrival stations in the correct sequential order.
* **Changeover Routes:** If no direct route exists, the application gathers a list of all routes departing from the origin and all routes arriving at the destination. It then iterates through these lists to find intersecting transfer stations. 
* **Time Validation:** To guarantee a viable transfer, the algorithm explicitly checks the timestamps, ensuring the arrival time of the first leg is strictly *before* the departure time of the second leg. If no valid intersection exists, a customized error message is returned.

### Requirement C: Administrator Operations & Alert System
Administrative functions are protected by Spring Security. Endpoints mapped under `/api/admin/**` strictly require a user with the `ROLE_ADMIN` authority.
* **Infrastructure Management:** Administrators can dynamically add new stations and trains to the database via secured POST requests.
* **Passenger Manifests:** Administrators can view all active bookings for a specific train through dedicated GET endpoints that query the `BookingRepository`.
* **Delay Alerts:** When an administrator marks a train as delayed, the `TrainService` fetches all bookings associated with that train. It extracts a distinct list of users (to prevent spamming a user who booked multiple tickets for family members) and triggers the `EmailService` to dispatch urgent delay notifications.

---

## 3. Getting Started & Test Environment

The application includes a `DataInitializer` class that automatically populates the H2 database on startup with a complete testing environment, including users, isolated stations, and trains with specific capacities.

**Pre-configured Test Accounts:**
* **Administrator:** `admin@trains.com` / `admin123` 
* **Standard Customer 1:** `john.doe@gmail.com` / `password` 
* **Standard Customer 2:** `jane.smith@gmail.com` / `password` 

*(Note: All API endpoints are secured via Basic Auth. You must provide the appropriate credentials in your request headers).*

---

## 4. API Endpoints & Usage Examples

### Route Searching functionality
Allows users to find paths between stations. Accessible by both Customers and Administrators.

**A. Find a Direct Route**
* **Endpoint:** `GET /api/routes/search?departureId=1&arrivalId=3`
* **Response (200 OK):**
```json
[
    {
        "train": { "name": "Flying Scotsman", "capacity": 200, "delayed": false, "id": 1 },
        "routeStations": [
            { "station": { "name": "London Kings Cross", "id": 1 }, "arrivalTime": "2026-05-10T18:21:32", "departureTime": "2026-05-10T18:36:32" },
            { "station": { "name": "Edinburgh Waverley", "id": 3 }, "arrivalTime": "2026-05-10T22:21:32", "departureTime": "2026-05-10T22:41:32" }
        ],
        "id": 1
    }
]
#  Railway Booking API

This API allows users to search routes, book tickets, and provides administrative operations for managing trains and stations.

---

##  Route Search

###  B. Find a Changeover Route

**Endpoint:**

```
GET /api/routes/search?departureId=1&arrivalId=4
```

**Response (200 OK):**
Returns a journey plan including:

* First leg
* Transfer station
* Second leg

```json
[
  {
    "firstLeg": { "id": 1, "train": { "name": "Flying Scotsman" } },
    "secondLeg": { "id": 2, "train": { "name": "Northern Pacer" } },
    "transferStation": { "name": "York", "id": 2 }
  }
]
```

---

###  C. No Possible Link

**Endpoint:**

```
GET /api/routes/search?departureId=6&arrivalId=7
```

**Response (400 Bad Request):**

```
No possible link found between the selected stations.
```

---

##  Booking Functionality

Allows customers to book tickets. Requires **Customer credentials**.

---

### A. Successful Booking

**Endpoint:**

```
POST /api/bookings
```

**Request Body:**

```json
{
  "userId": 2,
  "routeId": 1,
  "departureStationId": 1,
  "arrivalStationId": 2,
  "seats": 2
}
```

**Response (200 OK):**
*A confirmation email is automatically sent.*

```json
{
  "customer": {
    "email": "john.doe@gmail.com",
    "role": "CUSTOMER",
    "id": 2
  },
  "numberOfSeats": 2,
  "route": { "id": 1 },
  "departureStation": { "name": "London Kings Cross", "id": 1 },
  "arrivalStation": { "name": "York", "id": 2 },
  "id": 3
}
```

---

### B. Overbooking Prevention (Error Handling)

**Scenario:** Attempting to book more seats than available.

**Endpoint:**

```
POST /api/bookings
```

**Request Body:**

```json
{
  "userId": 2,
  "routeId": 3,
  "departureStationId": 5,
  "arrivalStationId": 3,
  "seats": 2
}
```

**Response (400 Bad Request):**

```
Overbooking prevented: Only 1 seats remaining on this route.
```

---

##  Administrator Operations

Requires **Administrator credentials**.

---

###  A. View Bookings for a Specific Train

**Endpoint:**

```
GET /api/admin/trains/3/bookings
```

**Response (200 OK):**

```json
[
  {
    "customer": {
      "email": "jane.smith@gmail.com",
      "id": 3
    },
    "numberOfSeats": 4,
    "departureStation": {
      "name": "Glasgow Central",
      "id": 5
    },
    "arrivalStation": {
      "name": "Edinburgh Waverley",
      "id": 3
    },
    "id": 2
  }
]
```

---

###  B. Mark Train as Delayed & Notify Customers

**Endpoint:**

```
POST /api/admin/trains/1/delay
```

**Response (200 OK):**

```
Train marked as delayed. Customers notified.
```

**Background Action:**

* System retrieves all users with active bookings on the train
* Sends notification emails to affected customers

---

### C. Create a New Station

**Endpoint:**

```
POST /api/admin/stations
```

**Request Body:**

```json
{
  "name": "Birmingham New Street"
}
```

**Response (200 OK):**

```json
{
  "name": "Birmingham New Street",
  "id": 8
}
```

---

###  D. Create a New Train

**Endpoint:**

```
POST /api/admin/trains
```

**Request Body:**

```json
{
  "name": "Virgin Pendolino",
  "capacity": 450
}
```

**Response (200 OK):**

```json
{
  "name": "Virgin Pendolino",
  "capacity": 450,
  "delayed": false,
  "id": 4
}
```

---

##  Summary

*  Search routes (direct or with changeovers)
*  Book tickets with seat validation
*  Prevent overbooking automatically
*  Admin tools for managing trains, stations, and delays
* Automated email notifications
