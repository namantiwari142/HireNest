import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProfileDropdown() {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const dashboardPath = user?.role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard';

  return (
    <div className="relative" ref={ref}>
      <button type="button" onClick={() => setOpen(!open)} className="flex items-center gap-2 p-1 rounded-lg hover:bg-white/5">
        <img
          src={user?.profileImageUrl || `https://ui-avatars.com/api/?name=${user?.name}&background=F59E0B&color=121212`}
          alt={user?.name}
          className="w-8 h-8 rounded-full object-cover"
        />
        <span className="hidden sm:block text-sm">{user?.name?.split(' ')[0]}</span>
      </button>
      {open && (
        <div className="absolute right-0 mt-2 w-48 bg-surface border border-white/10 rounded-xl shadow-xl overflow-hidden z-50">
          <div className="px-4 py-3 border-b border-white/5">
            <p className="font-medium text-sm">{user?.name}</p>
            <p className="text-xs text-muted">{user?.role}</p>
          </div>
          <Link to={dashboardPath} onClick={() => setOpen(false)} className="block px-4 py-2.5 text-sm hover:bg-white/5">Dashboard</Link>
          <Link to="/chat" onClick={() => setOpen(false)} className="block px-4 py-2.5 text-sm hover:bg-white/5">Messages</Link>
          {user?.role === 'APPLICANT' && (
            <Link to="/applicant/profile" onClick={() => setOpen(false)} className="block px-4 py-2.5 text-sm hover:bg-white/5">Profile</Link>
          )}
          <button
            type="button"
            onClick={() => { logout(); navigate('/'); setOpen(false); }}
            className="w-full text-left px-4 py-2.5 text-sm text-red-400 hover:bg-white/5"
          >
            Logout
          </button>
        </div>
      )}
    </div>
  );
}
