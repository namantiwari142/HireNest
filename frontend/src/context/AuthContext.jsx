import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { apiRequest } from '../api/client';

const AuthContext = createContext(null);

function normalizeUser(authData) {
  return {
    userId: authData.userId,
    email: authData.email || '',
    name: authData.name || '',
    role: String(authData.role || '').toUpperCase(),
    profileImageUrl: authData.profileImageUrl,
  };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('hirenest_user');
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState(true);

  const persist = useCallback((authData) => {
    const token = authData.token || localStorage.getItem('hirenest_token');
    if (!token) return null;
    const userData = normalizeUser(authData);
    localStorage.setItem('hirenest_token', token);
    localStorage.setItem('hirenest_user', JSON.stringify(userData));
    setUser(userData);
    return userData;
  }, []);

  const refreshSession = useCallback(async () => {
    const token = localStorage.getItem('hirenest_token');
    if (!token) {
      setUser(null);
      setLoading(false);
      return;
    }
    try {
      const res = await apiRequest('/api/auth/me');
      persist({ ...res.data, token });
    } catch {
      localStorage.removeItem('hirenest_token');
      localStorage.removeItem('hirenest_user');
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, [persist]);

  useEffect(() => {
    refreshSession();
  }, [refreshSession]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const res = await apiRequest('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });
      return persist(res.data);
    } finally {
      setLoading(false);
    }
  };

  const register = async (payload) => {
    setLoading(true);
    try {
      const res = await apiRequest('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      return persist(res.data);
    } finally {
      setLoading(false);
    }
  };

  const oauthLogin = (token, role, name, userId, email, profileImageUrl) => {
    return persist({
      token,
      role,
      name,
      userId: userId ? Number(userId) : null,
      email: email || '',
      profileImageUrl,
    });
  };

  const logout = () => {
    localStorage.removeItem('hirenest_token');
    localStorage.removeItem('hirenest_user');
    setUser(null);
  };

  const updateUser = (updates) => {
    setUser((prev) => {
      const next = normalizeUser({ ...prev, ...updates });
      localStorage.setItem('hirenest_user', JSON.stringify(next));
      return next;
    });
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        oauthLogin,
        logout,
        updateUser,
        refreshSession,
        isAuthenticated: !!user && !!localStorage.getItem('hirenest_token'),
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
