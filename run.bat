@echo off
setlocal

cd /d "%~dp0"

echo [1/3] Checking Docker...
docker --version >nul 2>&1
if errorlevel 1 (
  echo Docker not found. Please install/start Docker Desktop first.
  exit /b 1
)

echo [2/3] Starting PostgreSQL with docker compose...
docker compose up -d postgres
if errorlevel 1 (
  echo Failed to start postgres container.
  exit /b 1
)

echo [3/3] Waiting for PostgreSQL to be ready...
set /a max_tries=30
set /a tries=0

:wait_for_postgres
set /a tries+=1
docker exec modern-rest-postgres pg_isready -U postgres -d moderndb >nul 2>&1
if %errorlevel%==0 goto start_app
if %tries% GEQ %max_tries% (
  echo PostgreSQL did not become ready in time.
  exit /b 1
)
echo   - postgres not ready yet (try %tries%/%max_tries%), waiting 2s...
timeout /t 2 /nobreak >nul
goto wait_for_postgres

:start_app
echo PostgreSQL is ready.

rem Force local dev DB credentials for this launcher
set DB_USERNAME=postgres
set DB_PASSWORD=password

echo [4/4] Starting Spring Boot app...
call mvnw.cmd spring-boot:run

endlocal
