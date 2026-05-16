import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from './Logo';

export default function Footer() {
  const { isAuthenticated, user } = useAuth();
  const dashboardPath = user?.role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard';

  return (
    <footer className="bg-surface border-t border-white/5 mt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div className="md:col-span-2">
            <Logo />
            <p className="text-muted text-sm mt-4 max-w-sm">
              HireNest connects talented professionals with top companies. Your next career move starts here.
            </p>
          </div>
          <div>
            <h4 className="font-poppins font-semibold mb-4">Platform</h4>
            <ul className="space-y-2 text-sm text-muted">
              <li><Link to="/jobs" className="hover:text-accent transition-colors">Browse Jobs</Link></li>
              {isAuthenticated ? (
                <li><Link to={dashboardPath} className="hover:text-accent transition-colors">Dashboard</Link></li>
              ) : (
                <li><Link to="/login?tab=applicant" className="hover:text-accent transition-colors">Login</Link></li>
              )}
              <li><Link to="/about" className="hover:text-accent transition-colors">About Us</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="font-poppins font-semibold mb-4">For Employers</h4>
            <ul className="space-y-2 text-sm text-muted">
              {isAuthenticated && user?.role === 'RECRUITER' ? (
                <li><Link to="/recruiter/post-job" className="hover:text-accent transition-colors">Post a Job</Link></li>
              ) : !isAuthenticated ? (
                <li><Link to="/login?tab=recruiter" className="hover:text-accent transition-colors">Recruiter Login</Link></li>
              ) : null}
              <li><Link to="/jobs" className="hover:text-accent transition-colors">Find Talent</Link></li>
            </ul>
          </div>
        </div>
        <div className="mt-10 pt-6 border-t border-white/5 text-center text-sm text-muted">
          © {new Date().getFullYear()} HireNest. Built for SDE portfolios.
        </div>
      </div>
    </footer>
  );
}
