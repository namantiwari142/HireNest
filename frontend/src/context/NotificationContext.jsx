import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { apiRequest, getWebSocketUrl } from '../api/client';
import { useAuth } from './AuthContext';

const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const { user, isAuthenticated } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const fetchNotifications = useCallback(async () => {
    if (!isAuthenticated) return;
    try {
      const res = await apiRequest('/api/notifications');
      setNotifications(res.data || []);
      const countRes = await apiRequest('/api/notifications/unread-count');
      setUnreadCount(countRes.data?.count || 0);
    } catch {
      /* ignore */
    }
  }, [isAuthenticated]);

  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  useEffect(() => {
    if (!isAuthenticated || !user?.userId) return;

    const client = new Client({
      webSocketFactory: () => new SockJS(getWebSocketUrl()),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(`/topic/notifications/${user.userId}`, (msg) => {
          const notification = JSON.parse(msg.body);
          setNotifications((prev) => [notification, ...prev]);
          setUnreadCount((c) => c + 1);
        });
      },
    });
    client.activate();
    return () => client.deactivate();
  }, [isAuthenticated, user?.userId]);

  const markAsRead = async (id) => {
    await apiRequest(`/api/notifications/${id}/read`, { method: 'PATCH' });
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
    setUnreadCount((c) => Math.max(0, c - 1));
  };

  const markAllRead = async () => {
    await apiRequest('/api/notifications/read-all', { method: 'PATCH' });
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    setUnreadCount(0);
  };

  return (
    <NotificationContext.Provider value={{ notifications, unreadCount, fetchNotifications, markAsRead, markAllRead }}>
      {children}
    </NotificationContext.Provider>
  );
}

export const useNotifications = () => useContext(NotificationContext);
