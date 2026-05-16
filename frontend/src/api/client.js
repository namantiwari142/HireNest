function normalizeBaseUrl(url) {
  if (!url || typeof url !== 'string') return '';
  return url.trim().replace(/\/+$/, '');
}

const envApiUrl = normalizeBaseUrl(import.meta.env.VITE_API_URL);
const API_URL = envApiUrl || (import.meta.env.DEV ? 'http://localhost:8080' : '');

if (import.meta.env.PROD && !API_URL) {
  console.error('VITE_API_URL is not set. API requests will fail in production.');
}

function joinUrl(base, path) {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${base}${normalizedPath}`;
}

export async function apiRequest(path, options = {}) {
  if (!API_URL) {
    throw new Error('API URL is not configured. Set VITE_API_URL in your deployment environment.');
  }

  const token = localStorage.getItem('hirenest_token');
  const headers = {
    ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
    ...options.headers,
  };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(joinUrl(API_URL, path), { ...options, headers });
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    let message = data.message || 'Request failed';
    if (data.data && typeof data.data === 'object' && !Array.isArray(data.data)) {
      const first = Object.values(data.data)[0];
      if (first) message = String(first);
    }
    if (response.status === 403) {
      message = 'Access denied. Login as an applicant to use this feature.';
    }
    if (response.status === 401) {
      message = 'Session expired. Please login again.';
    }
    throw new Error(message);
  }

  if (data.success === false) {
    throw new Error(data.message || 'Request failed');
  }

  return data;
}

export const API_BASE = API_URL;

export function getWebSocketUrl() {
  const wsEnv = normalizeBaseUrl(import.meta.env.VITE_WS_URL);
  if (wsEnv) return wsEnv;
  if (!API_URL) return '';
  return joinUrl(API_URL, '/ws');
}

export function apiUrl(path) {
  return joinUrl(API_URL, path);
}
