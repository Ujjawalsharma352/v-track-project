# Step-by-Step: Run V-Track Backend in Eclipse

## 1. Prerequisites

- **Eclipse IDE** (Eclipse for Java EE Developers or Eclipse with "Eclipse IDE for Java Developers")
- **JDK 17** (Window → Preferences → Java → Installed JREs – add JDK 17)
- **Maven** (Eclipse usually includes Maven; check Window → Preferences → Maven)
- **MySQL** installed and running (default port 3306)

## 2. Import Maven Project

1. Open Eclipse.
2. **File → Import**.
3. Expand **Maven** → select **Existing Maven Projects** → **Next**.
4. **Browse** to the folder: `backend`.
5. Ensure the checkbox for `pom.xml` is checked.
6. Click **Finish**.
7. Wait for Maven to download dependencies (Progress view or status bar).

## 3. Configure MySQL

1. Open MySQL (e.g. MySQL Workbench or command line) and create a database (optional):
   ```sql
   CREATE DATABASE IF NOT EXISTS vtrack_db;
   ```
2. In the Eclipse Run Configuration, set:
   - `SPRING_DATASOURCE_USERNAME` = your MySQL username
   - `SPRING_DATASOURCE_PASSWORD` = your MySQL password
   - Optionally set `SPRING_DATASOURCE_URL` if your database is not at the local default.

3. Also add these environment variables:
   - `VTRACK_ADMIN_USERNAME` = your admin username
   - `VTRACK_ADMIN_PASSWORD` = a strong password
   - `VTRACK_AUTH_TOKEN` = a random secret
   - `VTRACK_CORS_ALLOWED_ORIGINS` = `http://localhost:5500,http://127.0.0.1:5500`

## 4. Run Spring Boot Application

1. In **Package Explorer**, expand `backend` → `src/main/java` → `com.vtrack`.
2. Right-click **VtrackBackendApplication.java**.
3. Select **Run As → Java Application**.
   - Or: **Run As → Spring Boot App** (if Spring Tools / STS is installed).
4. Check the **Console** for:
   - `Started VtrackBackendApplication in ...`
   - No errors about database connection.

## 5. Verify Backend

- Open browser: http://localhost:8080/api/dashboard  
  - Without token you should get **401 Unauthorized** (expected).
- Or use Postman: **Auth → Login** with the credentials you configured, then call **Get Dashboard** with the returned `X-Auth-Token`.

## Troubleshooting

- **Port 8080 already in use:** Set the `PORT` environment variable to `8081`.
- **MySQL connection refused:** Ensure MySQL service is running; check host/port/username/password.
- **Class not found / compilation errors:** Right-click project → **Maven → Update Project** (Force Update).
