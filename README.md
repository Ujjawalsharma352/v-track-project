# V-Track – Vehicle Entry & Exit Logging System

A full-stack application for logging vehicle entry and exit with dashboard, search, and export.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2 (Eclipse)
- **Frontend:** HTML, CSS, JavaScript (VS Code)
- **Database:** MySQL
- **API testing:** Postman


## Project Structure

- `backend/` — Spring Boot API and MySQL persistence
- `frontend/` — static HTML, CSS, and JavaScript
- `Dockerfile` — production build; packages the frontend with the backend
- `postman/` — API request collection


---

## Deploy to Railway

The root `Dockerfile` builds Spring Boot and packages the frontend into the same service, so the website and API use one HTTPS domain. Railway MySQL stores application data.

1. Push this repository to GitHub and create a Railway project from the repository. Railway will build the root `Dockerfile`.
2. Add a **MySQL** service to the same Railway project. Keep the database private.
3. In the application service's **Variables**, add the following (replace `MySQL` if you named the database service differently):

   ```text
   SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
   SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
   VTRACK_ADMIN_USERNAME=your-admin-name
   VTRACK_ADMIN_PASSWORD=<a unique, strong password>
   VTRACK_AUTH_TOKEN=<a unique random secret>
   VTRACK_CORS_ALLOWED_ORIGINS=https://${{RAILWAY_PUBLIC_DOMAIN}}
   ```

   Generate a random token locally with `openssl rand -hex 32`. Add the resulting secret and admin password only in Railway's Variables UI—never commit them to Git.
4. Deploy the service and generate a public domain in **Settings → Networking**.
5. Add `VTRACK_CORS_ALLOWED_ORIGINS=https://${{RAILWAY_PUBLIC_DOMAIN}}` to the application service variables and redeploy.
6. Visit the generated domain and log in using the admin credentials you configured. Enable database backups before using this for important records.

The application requires the admin username, admin password, and token secret at startup. There are no built-in production credentials. All `/api/**` routes except `/api/login` require the configured token.

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
   - Click **Next**, set **Root Directory** to `backend`.
   - Ensure `pom.xml` is selected, click **Finish**.
   - Wait for Maven to download dependencies.

3. **Configure MySQL**
   - Create a database (optional): run `database/schema.sql` in MySQL, or let Spring Boot create it.
   - In the Eclipse Run Configuration, set `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` for your local database. Optionally set `SPRING_DATASOURCE_URL`.

4. **Set required application environment variables**
   - In the Eclipse Run Configuration, add `VTRACK_ADMIN_USERNAME`, `VTRACK_ADMIN_PASSWORD`, and `VTRACK_AUTH_TOKEN`.
   - Set `VTRACK_CORS_ALLOWED_ORIGINS` to `http://localhost:5500,http://127.0.0.1:5500` when using Live Server.

5. **Run the application**
   - In **Package Explorer**, right-click the `backend` project.
   - **Run As → Spring Boot App** (or **Run As → Java Application** and choose `VtrackBackendApplication`).
   - Backend and frontend run at **http://localhost:8080**.

6. **Verify**
   - Open browser: http://localhost:8080/api/dashboard  
   - You should get 401 (Unauthorized) without a token, or log in through the frontend and use the returned token.

---

## Step-by-Step: Run Frontend in VS Code

1. **Prerequisites**
   - VS Code
   - Backend running on http://localhost:8080

2. **Open folder**
   - **File → Open Folder** → select `frontend`.

3. **Serve the frontend (choose one)**
   - **Option A – Live Server (recommended)**  
     - Install extension **Live Server**.  
     - Right-click `index.html` → **Open with Live Server**.  
     - Frontend opens at e.g. http://127.0.0.1:5500 (or the port shown).
   - **Option B – Open file directly**  
     - Double-click `index.html` to open in browser (some features may fail due to CORS/file protocol).

4. **Login**
   - Go to **Login** page.
   - Use the admin credentials configured in the backend environment.
   - After login you can use Dashboard, Logs, Search, and Export.

---

## Postman Collection

1. Import `postman/V-Track-API.postman_collection.json` into Postman.
2. **Variables:** `baseUrl` = `http://localhost:8080/api`; set `adminUsername` and `adminPassword`, then copy the returned login token into `token`.
3. **Login:** Run **Auth → Login**; copy the `token` from response and set collection variable `token` if different.
4. **Protected APIs:** Use header `X-Auth-Token: {{token}}` (or set in collection).

### Example requests

- **POST** `/api/login`  
  Body: `{ "username": "{{adminUsername}}", "password": "{{adminPassword}}" }`
- **POST** `/api/entry`  
  Body: `{ "vehicleNumber": "MH-12-AB-1234", "vehicleType": "Car", "driverName": "John", "purpose": "Visit" }`
- **POST** `/api/exit/MH-12-AB-1234`
- **GET** `/api/logs` (header: `X-Auth-Token: {{token}}`)
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

---

## License

For educational/placement use. Modify as needed for your project.
