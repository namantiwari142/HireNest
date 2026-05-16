import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, roles }) {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !roles.includes(user.role)) {
    const redirect = user.role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard';
    return <Navigate to={redirect} replace />;
  }

  return children;
}
