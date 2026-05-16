import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function OAuthCallbackPage() {
  const [params] = useSearchParams();
  const { oauthLogin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const token = params.get('token');
    const role = params.get('role');
    const userId = params.get('userId');
    const name = params.get('name');
    const email = params.get('email');

    if (token && role) {
      oauthLogin(token, role, name || 'User', userId, email, null);
      toast.success('Logged in successfully');
      navigate(role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard');
    } else {
      toast.error('OAuth login failed');
      navigate('/login');
    }
  }, [params, oauthLogin, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <p className="text-muted">Completing login...</p>
    </div>
  );
}
