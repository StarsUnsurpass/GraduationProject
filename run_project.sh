#!/bin/bash

# Kill ports 8080 and 8081 just in case
echo "Stopping existing processes on ports 8080 and 8081..."
fuser -k 8080/tcp 2>/dev/null
fuser -k 8081/tcp 2>/dev/null

# Start Backend
echo "Starting Backend..."
cd backend/power-fault-analysis
# Use -Dspring-boot.run.jvmArguments to show logs clearly
./mvnw clean spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
cd ../..

echo "Backend starting in background (Logs: backend/power-fault-analysis/backend.log)..."
echo "Waiting for Backend to initialize (30 seconds)..."
sleep 30

# Check if Backend is alive
if ! ps -p $BACKEND_PID > /dev/null; then
   echo "ERROR: Backend failed to start. Check backend/power-fault-analysis/backend.log"
   exit 1
fi

# Start Frontend
echo "Starting Frontend..."
cd frontend/power-fault-analysis-frontend
# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    npm install
fi
npm run dev &
FRONTEND_PID=$!
cd ../..

echo "---------------------------------------------------"
echo "Application ready!"
echo "Frontend: http://localhost:8080"
echo "Backend:  http://localhost:8081"
echo "Health:   http://localhost:8081/api/health"
echo "---------------------------------------------------"
echo "Press Ctrl+C to stop."

trap "kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait
