# InvestSense AI

Behavior-Aware and Explainable Investment Recommendation System for Indian Investors.

## Stack
- Frontend: React + Vite + CSS
- Backend: Spring Boot + Java 17
- Database: H2 by default (easy demo) / MySQL optional
- Charts: Recharts

## Run backend
1. Open `backend` in VS Code.
2. Make sure Java 17+ and Maven are installed.
3. Run:
   mvn spring-boot:run
Backend: http://localhost:8080

## Run frontend
1. Open another VS Code terminal.
2. `cd frontend`
3. `npm install`
4. `npm run dev`
Frontend: http://localhost:5173

## Optional MySQL
The backend currently uses H2 so the project works immediately.
To switch to MySQL, create a database named `investsense` and replace the datasource properties in:
backend/src/main/resources/application.properties

## Demo flow
Register -> Login -> Financial Assessment -> Behavioral Assessment -> Analyze -> Dashboard -> What-if Simulator.

Important:
This is an educational project prototype. Recommendations are illustrative and are not financial advice.
