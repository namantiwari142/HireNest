import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from './Logo';
import NotificationDropdown from './NotificationDropdown';
import ProfileDropdown from './ProfileDropdown';

export default function Navbar({ transparent = false }) {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  return (
    <nav className={`sticky top-0 z-50 ${transparent ? 'bg-transparent' : 'bg-background/90 backdrop-blur-md border-b border-white/5'}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <Logo />
        <div className="hidden md:flex items-center gap-8 text-sm">
          <Link to="/jobs" className="text-muted hover:text-white transition-colors">Browse Jobs</Link>
          <Link to="/about" className="text-muted hover:text-white transition-colors">About</Link>
        </div>
        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <NotificationDropdown />
              <ProfileDropdown />
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm text-muted hover:text-white transition-colors px-3 py-2">Login</Link>
              <button type="button" onClick={() => navigate('/register')} className="btn-primary text-sm">
                Get Started
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
