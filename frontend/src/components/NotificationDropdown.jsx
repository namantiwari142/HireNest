import { useState, useRef, useEffect } from 'react';
import { useNotifications } from '../context/NotificationContext';
import { timeAgo } from '../utils/format';

export default function NotificationDropdown() {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  const { notifications, unreadCount, markAsRead, markAllRead } = useNotifications();

  useEffect(() => {
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className="relative p-2 rounded-lg hover:bg-white/5 transition-colors"
        aria-label="Notifications"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        {unreadCount > 0 && (
          <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-accent text-background text-[10px] font-bold rounded-full flex items-center justify-center">
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>
      {open && (
        <div className="absolute right-0 mt-2 w-80 bg-surface border border-white/10 rounded-xl shadow-xl overflow-hidden z-50">
          <div className="flex items-center justify-between px-4 py-3 border-b border-white/5">
            <span className="font-poppins font-semibold">Notifications</span>
            {unreadCount > 0 && (
              <button type="button" onClick={markAllRead} className="text-xs text-accent hover:underline">
                Mark all read
              </button>
            )}
          </div>
          <div className="max-h-80 overflow-y-auto">
            {notifications.length === 0 ? (
              <p className="p-4 text-sm text-muted text-center">No notifications yet</p>
            ) : (
              notifications.slice(0, 10).map((n) => (
                <button
                  key={n.id}
                  type="button"
                  onClick={() => !n.read && markAsRead(n.id)}
                  className={`w-full text-left px-4 py-3 border-b border-white/5 hover:bg-white/5 transition-colors ${!n.read ? 'bg-accent/5' : ''}`}
                >
                  <p className="text-sm font-medium">{n.title}</p>
                  <p className="text-xs text-muted mt-0.5 line-clamp-2">{n.message}</p>
                  <p className="text-[10px] text-muted mt-1">{timeAgo(n.createdAt)}</p>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}
