/**
 * V-Track Frontend - API helper
 * Use the local API during frontend-only development and same-origin requests
 * when Spring Boot serves the frontend in production.
 */

const isLocalFrontend = window.location.protocol === 'file:' ||
  (['localhost', '127.0.0.1'].includes(window.location.hostname) &&
    window.location.port !== '8080');
const API_BASE = isLocalFrontend ? 'http://localhost:8080/api' : '/api';

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

function requireLogin(page) {
  if (isLoggedIn()) return true;
  window.location.replace(`login.html?returnTo=${encodeURIComponent(page)}`);
  return false;
}

function escapeHtmlValue(value) {
  return String(value ?? '').replace(/[&<>"']/g, (character) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
  })[character]);
}

function renderLogStatus(log) {
  const status = String(log.status || 'UNKNOWN').toUpperCase();
  const statusClass = status === 'IN' ? 'status-in' : status === 'OUT' ? 'status-out' : 'status-unknown';
  const overstay = log.overstayAlert
    ? '<span class="status-pill status-overstay">Overstay</span>'
    : '';
  return `<span class="status-pill ${statusClass}">${escapeHtmlValue(status)}</span>${overstay}`;
}

function createPagedLogList({
  tableBody,
  searchInput,
  summary,
  previousButton,
  nextButton,
  columnCount,
  renderRow,
  emptyMessage,
  pageSize = 10,
}) {
  let records = [];
  let currentPage = 1;

  function showMessage(message) {
    tableBody.replaceChildren();
    const row = document.createElement('tr');
    const cell = document.createElement('td');
    cell.colSpan = columnCount;
    cell.className = 'table-empty';
    cell.textContent = message;
    row.appendChild(cell);
    tableBody.appendChild(row);
    summary.textContent = '';
    previousButton.disabled = true;
    nextButton.disabled = true;
  }

  function render() {
    const query = searchInput.value.trim().toLocaleLowerCase();
    const filteredRecords = records.filter((log) => [
      log.vehicleNumber,
      log.vehicleType,
      log.driverName,
      log.purpose,
      log.status,
    ].some((value) => String(value ?? '').toLocaleLowerCase().includes(query)));

    const pageCount = Math.max(1, Math.ceil(filteredRecords.length / pageSize));
    currentPage = Math.min(currentPage, pageCount);

    if (filteredRecords.length === 0) {
      showMessage(records.length === 0 ? emptyMessage : 'No records match your search.');
      return;
    }

    const firstRecord = (currentPage - 1) * pageSize;
    const visibleRecords = filteredRecords.slice(firstRecord, firstRecord + pageSize);
    tableBody.innerHTML = visibleRecords.map((log) => renderRow(log)).join('');
    summary.textContent =
      `Showing ${firstRecord + 1}–${firstRecord + visibleRecords.length} of ${filteredRecords.length} · Page ${currentPage} of ${pageCount}`;
    previousButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === pageCount;
  }

  searchInput.addEventListener('input', () => {
    currentPage = 1;
    render();
  });
  previousButton.addEventListener('click', () => {
    currentPage -= 1;
    render();
  });
  nextButton.addEventListener('click', () => {
    currentPage += 1;
    render();
  });

  return {
    setRecords(nextRecords) {
      if (!Array.isArray(nextRecords)) {
        throw new TypeError('Expected vehicle history to be an array.');
      }
      records = nextRecords;
      currentPage = 1;
      render();
    },
    showMessage,
  };
}

function initNavigation() {
  const nav = document.querySelector('.nav');
  const links = nav?.querySelector('.nav-links');
  if (!nav || !links) return;

  const authenticated = isLoggedIn();
  const items = authenticated
    ? [
      ['dashboard.html', 'Dashboard'],
      ['entry.html', 'Entry'],
      ['exit.html', 'Exit'],
      ['logs.html', 'Logs'],
    ]
    : [
      ['index.html', 'Home'],
      ['login.html', 'Login'],
    ];

  links.replaceChildren();
  items.forEach(([href, label]) => {
    const link = document.createElement('a');
    link.href = href;
    link.textContent = label;
    const isCurrentPage = new URL(href, window.location.href).pathname === window.location.pathname;
    if (isCurrentPage) {
      link.classList.add('active');
      link.setAttribute('aria-current', 'page');
    }
    links.appendChild(link);
  });

  if (authenticated) {
    const logoutLink = document.createElement('a');
    logoutLink.href = '#';
    logoutLink.textContent = 'Logout';
    logoutLink.id = 'logoutLink';
    logoutLink.addEventListener('click', (event) => {
      event.preventDefault();
      localStorage.removeItem('vtrack_token');
      window.location.href = 'login.html';
    });
    links.appendChild(logoutLink);
  }

  if (!links.id) links.id = 'primaryNavLinks';
  const toggle = document.createElement('button');
  toggle.type = 'button';
  toggle.className = 'nav-toggle';
  toggle.setAttribute('aria-label', 'Toggle navigation');
  toggle.setAttribute('aria-controls', links.id);
  toggle.setAttribute('aria-expanded', 'false');
  toggle.innerHTML = '<span></span><span></span><span></span>';
  toggle.addEventListener('click', () => {
    const isOpen = nav.classList.toggle('nav-open');
    toggle.setAttribute('aria-expanded', String(isOpen));
  });
  links.addEventListener('click', (event) => {
    if (event.target.closest('a') && nav.classList.contains('nav-open')) {
      nav.classList.remove('nav-open');
      toggle.setAttribute('aria-expanded', 'false');
    }
  });
  nav.appendChild(toggle);
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

initNavigation();
