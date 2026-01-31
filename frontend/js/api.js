/**
 * V-Track Frontend - API helper
 * Base URL for backend (Spring Boot runs on port 8080)
 */

const API_BASE = 'http://localhost:8080/api';

/**
 * Get auth token from localStorage (set after login)
 */
function getAuthToken() {
  return localStorage.getItem('vtrack_token');
}

/**
 * Check if user is logged in
 */
function isLoggedIn() {
  return !!getAuthToken();
}

/**
 * Redirect to login if not authenticated
 */
function requireAuth() {
  if (!isLoggedIn()) {
    window.location.href = 'login.html';
    return false;
  }
  return true;
}

/**
 * Fetch with optional auth header
 */
function apiFetch(url, options = {}) {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };
  if (token) {
    headers['X-Auth-Token'] = token;
  }
  return fetch(url, { ...options, headers });
}

/**
 * POST /api/login
 */
async function login(username, password) {
  const res = await fetch(`${API_BASE}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  const data = await res.json();
  if (data.success && data.token) {
    localStorage.setItem('vtrack_token', data.token);
  }
  return data;
}

/**
 * POST /api/entry
 */
async function addEntry(body) {
  const res = await apiFetch(`${API_BASE}/entry`, {
    method: 'POST',
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error || res.statusText);
  }
  return res.json();
}

/**
 * POST /api/exit/{vehicleNumber}
 */
async function markExit(vehicleNumber) {
  const res = await apiFetch(`${API_BASE}/exit/${encodeURIComponent(vehicleNumber)}`, {
    method: 'POST',
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error || res.statusText);
  }
  return res.json();
}

/**
 * GET /api/logs (requires auth)
 */
async function getAllLogs() {
  const res = await apiFetch(`${API_BASE}/logs`);
  if (!res.ok) throw new Error('Failed to fetch logs');
  return res.json();
}

/**
 * GET /api/logs/today (requires auth)
 */
async function getTodayLogs() {
  const res = await apiFetch(`${API_BASE}/logs/today`);
  if (!res.ok) throw new Error('Failed to fetch today logs');
  return res.json();
}

/**
 * GET /api/search?vehicleNumber=&date= (requires auth)
 */
async function searchLogs(vehicleNumber, date) {
  const params = new URLSearchParams();
  if (vehicleNumber) params.set('vehicleNumber', vehicleNumber);
  if (date) params.set('date', date);
  const res = await apiFetch(`${API_BASE}/search?${params}`);
  if (!res.ok) throw new Error('Search failed');
  return res.json();
}

/**
 * GET /api/dashboard (requires auth)
 */
async function getDashboard() {
  const res = await apiFetch(`${API_BASE}/dashboard`);
  if (!res.ok) throw new Error('Failed to fetch dashboard');
  return res.json();
}

/**
 * GET /api/analytics/most-visited (requires auth)
 */
async function getMostVisited() {
  const res = await apiFetch(`${API_BASE}/analytics/most-visited`);
  if (!res.ok) throw new Error('Failed to fetch analytics');
  return res.json();
}

/**
 * GET /api/export - download CSV (requires auth)
 */
function exportCsv() {
  const token = getAuthToken();
  const url = `${API_BASE}/export`;
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'vtrack-logs.csv');
  if (token) {
    // Fetch with token and create blob link (same-origin or CORS with credentials)
    fetch(url, { headers: { 'X-Auth-Token': token } })
      .then(r => r.text())
      .then(csv => {
        const blob = new Blob([csv], { type: 'text/csv' });
        const u = URL.createObjectURL(blob);
        link.href = u;
        link.download = 'vtrack-logs.csv';
        link.click();
        URL.revokeObjectURL(u);
      })
      .catch(() => alert('Export failed. Check console.'));
    return;
  }
  link.click();
}

/**
 * Format duration in minutes to "Xh Ym"
 */
function formatDuration(minutes) {
  if (minutes == null) return '-';
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  if (h > 0) return `${h}h ${m}m`;
  return `${m}m`;
}

/**
 * Format LocalDateTime string for display
 */
function formatDateTime(str) {
  if (!str) return '-';
  const d = new Date(str);
  return d.toLocaleString();
}
