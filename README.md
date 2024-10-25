# Drone Management System

## Overview

This application manages drones and their medication loads. It allows for the registration of drones, loading of medications onto drones, and monitoring their statuses.

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database (for development)
- Lombok
- Maven

## Prerequisites

- Java Development Kit (JDK) 17
- Maven 3.6 or higher
  Features

## Features

- Register drones with serial numbers and specifications.
- Load drones with medications while checking weight limits and battery capacity.
- View loaded medications and check the status of drones.
- Automated state management for drones based on operations.

## Setup Instructions

1. **Clone the repository:**

git clone [https://github.com/ninaclaudine/drone_Nina_Casaje.git](https://github.com/ninaclaudine/drone_Nina_Casaje.git)

2. **Import and Build Project:**
Make sure you have Maven installed
- You can Run it via the Intellij IDE or Eclipse.
- OR; just use this command in the terminal 'mvn clean install'.

3. **Import and Build Project:**
  You can Run it via the Intellij IDE or Eclipse.
- OR; just use this command in the terminal "mvn spring-boot:run"

4. **Access the Application:**
- Once the application is running, you can access the API at http://localhost:8080.

5. **Database H2 Setup:**
- Some of the data/information are pre-loaded in the database.
- Access the H2 console for database http://localhost:8080/h2-console/
- You can see below attached screenshot for the login credentials

**Credentials for the H2 Database login:**
1. Driver Class: org.h2.Driver
2. JDBC URL: jdbc:h2:file:./data/testdb

it should be like this:
![image](https://github.com/user-attachments/assets/58a9b072-813e-4856-a1b1-27ca177fdb70)


3. Then test the connection if success click the connect and start the transaction/Testing.
## API Endpoints

Postman Collection is also included in the github.

1. **Register a Drone:**

   **Endpoint:** POST /api/drones
   **Request Body:**
   ![image](https://github.com/user-attachments/assets/293ee571-62c4-432d-bf73-8196e5c85f1f)


   **Response:** Returns the registered drone.

2. **Load a Drone with Medication:**
   **Endpoint:** POST /api/drones/{droneId}/load/{medicationId}
   **Response:** Returns the updated drone.
3. **View Drone Load Medications**
   **Endpoint:** GET /api/drones/{droneId}/medications
   **Response:** Returns a list of medications loaded on the drone.
4. **Check Drone Status**
   **Endpoint:** GET /api/drones/{droneId}/status
   **Response:** Returns true if the drone can load more medication, otherwise false.
5. **View All Drones**
   **Endpoint:** GET /api/drones
   **Response:** Returns a list of all registered drones.
   
## Error Handling

The application handles various exceptions:

**BatterLevelLowException:** Thrown if an operation cannot proceed due to low battery levels.
**WeightLimitExceededException:** Thrown if the total medication weight exceeds the drone's weight limit.
**IllegalArgumentException:** Thrown if a drone with the same serial number already exists during registration.


