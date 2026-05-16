import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/** Redirect logged-in users away from login/register pages */
export default function GuestRoute({ children }) {
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated && user?.role) {
    const home =
      user.role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard';
    return <Navigate to={home} replace />;
  }

  return children;
}
