# V-Track – Vehicle Entry & Exit Logging System

A full-stack application for logging vehicle entry and exit with dashboard, search, and export.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2 (Eclipse)
- **Frontend:** HTML, CSS, JavaScript (VS Code)
- **Database:** MySQL
- **API testing:** Postman


## Project Structure

V-TRACK_APPLICATION/
├── vtrack-backend/          # Spring Boot REST API
│   ├── src/main/java/com/vtrack/
│   │   ├── config/          # Security, CORS
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Global exception handler
│   │   ├── repository/      # JPA repositories
│   │   ├── security/        # Token auth filter
│   │   └── service/         # Business logic
│   └── pom.xml
├── vtrack-frontend/         # HTML/CSS/JS
│   ├── css/styles.css
│   ├── js/api.js
│   ├── index.html
│   ├── login.html
│   ├── entry.html
│   ├── exit.html
│   ├── dashboard.html
│   ├── logs.html
│   └── search.html
├── database/schema.sql      # Sample MySQL schema
├── postman/                 # Postman collection
└── README.md


---

## Step-by-Step: Run Backend in Eclipse

1. **Prerequisites**
   - Eclipse IDE for Java EE Developers (or Eclipse with Spring Tools)
   - JDK 17
   - Maven (Eclipse usually has embedded Maven)
   - MySQL server running locally

2. **Import the project**
   - Open Eclipse.
   - **File → Import → Maven → Existing Maven Projects**.
   - Click **Next**, set **Root Directory** to `V-TRACK_APPLICATION/vtrack-backend`.
   - Ensure `pom.xml` is selected, click **Finish**.
   - Wait for Maven to download dependencies.

3. **Configure MySQL**
   - Create a database (optional): run `database/schema.sql` in MySQL, or let Spring Boot create it.
   - In `vtrack-backend/src/main/resources/application.properties`, set:
     - `spring.datasource.username` = your MySQL username (e.g. `root`)
     - `spring.datasource.password` = your MySQL password

4. **Run the application**
   - In **Package Explorer**, right-click `vtrack-backend` project.
   - **Run As → Spring Boot App** (or **Run As → Java Application** and choose `VtrackBackendApplication`).
   - Backend runs at **http://localhost:8080**.

5. **Verify**
   - Open browser: http://localhost:8080/api/dashboard  
   - You should get 401 (Unauthorized) without token, or use Postman with `X-Auth-Token: vtrack-admin-token` after login.

---

## Step-by-Step: Run Frontend in VS Code

1. **Prerequisites**
   - VS Code
   - Backend running on http://localhost:8080

2. **Open folder**
   - **File → Open Folder** → select `V-TRACK_APPLICATION/vtrack-frontend`.

3. **Serve the frontend (choose one)**
   - **Option A – Live Server (recommended)**  
     - Install extension **Live Server**.  
     - Right-click `index.html` → **Open with Live Server**.  
     - Frontend opens at e.g. http://127.0.0.1:5500 (or the port shown).
   - **Option B – Open file directly**  
     - Double-click `index.html` to open in browser (some features may fail due to CORS/file protocol).

4. **Login**
   - Go to **Login** page.
   - Use **admin** / **admin123**.
   - After login you can use Dashboard, Logs, Search, and Export.

---

## Postman Collection

1. Import `postman/V-Track-API.postman_collection.json` into Postman.
2. **Variables:** `baseUrl` = `http://localhost:8080/api`, `token` = `vtrack-admin-token`.
3. **Login:** Run **Auth → Login**; copy the `token` from response and set collection variable `token` if different.
4. **Protected APIs:** Use header `X-Auth-Token: {{token}}` (or set in collection).

### Example requests

- **POST** `/api/login`  
  Body: `{ "username": "admin", "password": "admin123" }`
- **POST** `/api/entry`  
  Body: `{ "vehicleNumber": "MH-12-AB-1234", "vehicleType": "Car", "driverName": "John", "purpose": "Visit" }`
- **POST** `/api/exit/MH-12-AB-1234`
- **GET** `/api/logs` (header: `X-Auth-Token: vtrack-admin-token`)
- **GET** `/api/logs/today`
- **GET** `/api/search?vehicleNumber=MH&date=2025-01-31`
- **GET** `/api/dashboard`
- **GET** `/api/analytics/most-visited`
- **GET** `/api/export`

---

## Sample MySQL Schema

See `database/schema.sql`. Spring Boot with `spring.jpa.hibernate.ddl-auto=update` will create/update tables automatically; use the SQL file only if you want to create the database manually.

---

## API Summary

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/login | No | Admin login, returns token |
| POST | /api/entry | No | Add vehicle entry |
| POST | /api/exit/{vehicleNumber} | No | Mark exit, compute duration |
| GET | /api/logs | Yes | All logs |
| GET | /api/logs/today | Yes | Today's entries |
| GET | /api/search?vehicleNumber=&date= | Yes | Search by vehicle/date |
| GET | /api/dashboard | Yes | Today entries, exits, currently parked |
| GET | /api/analytics/most-visited | Yes | Most frequently visited vehicles |
| GET | /api/export | Yes | Export logs as CSV |

---

## Business Rules

- **Duration:** exitTime − entryTime (in minutes).
- **Overstay:** Duration > 6 hours is flagged (overstayAlert in response; highlighted in frontend).
- **One active entry:** Only one active **IN** entry per vehicle; must mark exit before next entry.

---

## Default Credentials

- **Username:** admin  
- **Password:** admin123  

Change in `application.properties` (or use environment variables in production).

---

## License

For educational/placement use. Modify as needed for your project.
