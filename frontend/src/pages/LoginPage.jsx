import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { API_BASE } from '../api/client';
import Logo from '../components/Logo';

export default function LoginPage() {
  const [searchParams] = useSearchParams();
  const initialTab = searchParams.get('tab') === 'recruiter' ? 'recruiter' : 'applicant';
  const [tab, setTab] = useState(initialTab);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, logout, loading } = useAuth();
  const navigate = useNavigate();

  const expectedRole = tab === 'recruiter' ? 'RECRUITER' : 'APPLICANT';

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const user = await login(email, password);
      if (user.role !== expectedRole) {
        logout();
        toast.error(
          tab === 'recruiter'
            ? 'This account is not a recruiter. Switch to Applicant login.'
            : 'This account is not an applicant. Switch to Recruiter login.'
        );
        return;
      }
      toast.success('Welcome back!');
      navigate(tab === 'recruiter' ? '/recruiter/dashboard' : '/applicant/dashboard');
    } catch (err) {
      toast.error(err.message);
    }
  };

  const oauthUrl = (provider) =>
    `${API_BASE}/oauth2/authorization/${provider}?role=${expectedRole}`;

  return (
    <div className="min-h-screen flex items-center justify-center px-4 bg-background">
      <div className="w-full max-w-md">
        <div className="text-center mb-8"><Logo className="justify-center" /></div>
        <div className="card">
          <h1 className="font-poppins text-2xl font-bold text-center">Welcome to HireNest</h1>
          <p className="text-muted text-sm text-center mt-2">Sign in to your account</p>

          <div className="flex mt-6 p-1 bg-background rounded-lg border border-white/10">
            <button
              type="button"
              onClick={() => setTab('applicant')}
              className={`flex-1 py-2.5 text-sm font-medium rounded-md transition-all ${
                tab === 'applicant' ? 'bg-accent text-background' : 'text-muted hover:text-white'
              }`}
            >
              Applicant Login
            </button>
            <button
              type="button"
              onClick={() => setTab('recruiter')}
              className={`flex-1 py-2.5 text-sm font-medium rounded-md transition-all ${
                tab === 'recruiter' ? 'bg-accent text-background' : 'text-muted hover:text-white'
              }`}
            >
              Recruiter Login
            </button>
          </div>

          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-field"
              required
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-field"
              required
            />
            <button type="submit" disabled={loading} className="btn-primary w-full">
              {loading ? 'Signing in...' : `Sign in as ${tab === 'recruiter' ? 'Recruiter' : 'Applicant'}`}
            </button>
          </form>

          <div className="mt-6 flex gap-3">
            <a href={oauthUrl('google')} className="flex-1 btn-outline text-center text-sm py-2">Google</a>
            <a href={oauthUrl('github')} className="flex-1 btn-outline text-center text-sm py-2">GitHub</a>
          </div>

          <p className="text-center text-sm text-muted mt-6">
            New here?{' '}
            <Link to={`/register?tab=${tab}`} className="text-accent hover:underline">
              Create {tab} account
            </Link>
          </p>
          <p className="text-center text-xs text-muted mt-4">
            {tab === 'applicant'
              ? 'Demo: applicant@hirenest.com / applicant123'
              : 'Demo: recruiter@hirenest.com / recruiter123'}
          </p>
        </div>
      </div>
    </div>
  );
}
