const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem('hirenest_token');
  const headers = {
    ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
    ...options.headers,
  };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(`${API_URL}${path}`, { ...options, headers });
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
