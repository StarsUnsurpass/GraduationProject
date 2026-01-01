#!/bin/bash

# Kill ports 8080 and 8081 just in case
echo "Stopping existing processes on ports 8080 and 8081..."
fuser -k 8080/tcp 2>/dev/null
fuser -k 8081/tcp 2>/dev/null

# Start Backend
echo "Starting Backend..."
cd backend/power-fault-analysis
./mvnw clean spring-boot:run &
BACKEND_PID=$!
cd ../..

# Wait for Backend to be ready (naive wait)
echo "Waiting for Backend to initialize..."
sleep 15

# Start Frontend
echo "Starting Frontend..."
cd frontend/power-fault-analysis-frontend
npm install
npm run dev &
FRONTEND_PID=$!
cd ../..

echo "Application started!"
echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo "Access Frontend at http://localhost:8080"
echo "Access Backend at http://localhost:8081"
echo "Press Ctrl+C to stop both."

# Trap Ctrl+C to kill both processes
trap "kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait