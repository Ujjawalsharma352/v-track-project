# Step-by-Step: Run V-Track Frontend in VS Code

## 1. Prerequisites

- **VS Code** (or Cursor)
- **Backend running** at http://localhost:8080 (see RUN_BACKEND_ECLIPSE.md)

## 2. Open Frontend Folder

1. Open VS Code.
2. **File → Open Folder**.
3. Select folder: `V-TRACK_APPLICATION/vtrack-frontend`.
4. Click **Select Folder**.

## 3. Serve the Frontend (Live Server – Recommended)

1. Install **Live Server** extension:
   - **View → Extensions** (or Ctrl+Shift+X).
   - Search for **Live Server** (by Ritwick Dey).
   - Click **Install**.
2. In the Explorer, right-click **index.html**.
3. Select **Open with Live Server**.
4. Browser opens at e.g. **http://127.0.0.1:5500** (or the port shown in status bar).

## 4. Use the Application

1. **Home:** index.html – overview and navigation.
2. **Login:** login.html – use **admin** / **admin123**.
3. **Entry:** entry.html – record vehicle entry (confirmation popup).
4. **Exit:** exit.html – enter vehicle number, mark exit (shows duration).
5. **Dashboard:** dashboard.html – today’s entries, exits, currently parked (requires login).
6. **Logs:** logs.html – view all logs, filter by date/vehicle, export CSV (requires login).
7. **Search:** search.html – search by vehicle number and/or date (requires login).

## 5. If API Calls Fail

- Ensure backend is running at **http://localhost:8080**.
- Open browser **Developer Tools (F12) → Console** and **Network** to see errors.
- If you see CORS errors, ensure `application.properties` has:
  - `vtrack.cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500`

## Alternative: Open HTML Directly

- Double-click `index.html` to open in browser (file:// protocol).  
- Some browsers may block requests to localhost; using **Live Server** is recommended.
