import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { apiUrl } from '../api/client';
import Logo from '../components/Logo';

export default function RegisterPage() {
  const [searchParams] = useSearchParams();
  const initialTab = searchParams.get('tab') === 'recruiter' ? 'recruiter' : 'applicant';
  const [tab, setTab] = useState(initialTab);
  const [form, setForm] = useState({ name: '', email: '', password: '', companyName: '' });
  const { register, loading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        name: form.name,
        email: form.email,
        password: form.password,
        role: tab === 'recruiter' ? 'RECRUITER' : 'APPLICANT',
        companyName: tab === 'recruiter' ? form.companyName : undefined,
      };
      const user = await register(payload);
      toast.success('Account created!');
      navigate(user.role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard');
    } catch (err) {
      toast.error(err.message);
    }
  };

  const oauthUrl = (provider) =>
    apiUrl(`/oauth2/authorization/${provider}?role=${tab === 'recruiter' ? 'RECRUITER' : 'APPLICANT'}`);

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8"><Logo className="justify-center" /></div>
        <div className="card">
          <h1 className="font-poppins text-2xl font-bold text-center">Create your account</h1>

          <div className="flex mt-6 p-1 bg-background rounded-lg border border-white/10">
            <button
              type="button"
              onClick={() => setTab('applicant')}
              className={`flex-1 py-2.5 text-sm font-medium rounded-md transition-all ${
                tab === 'applicant' ? 'bg-accent text-background' : 'text-muted hover:text-white'
              }`}
            >
              Applicant
            </button>
            <button
              type="button"
              onClick={() => setTab('recruiter')}
              className={`flex-1 py-2.5 text-sm font-medium rounded-md transition-all ${
                tab === 'recruiter' ? 'bg-accent text-background' : 'text-muted hover:text-white'
              }`}
            >
              Recruiter
            </button>
          </div>

          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            <input placeholder="Full name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="input-field" required />
            <input type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="input-field" required />
            <input type="password" placeholder="Password (min 6 chars)" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} className="input-field" required minLength={6} />
            {tab === 'recruiter' && (
              <input placeholder="Company name" value={form.companyName} onChange={(e) => setForm({ ...form, companyName: e.target.value })} className="input-field" required />
            )}
            <button type="submit" disabled={loading} className="btn-primary w-full">
              {loading ? 'Creating...' : `Register as ${tab === 'recruiter' ? 'Recruiter' : 'Applicant'}`}
            </button>
          </form>
          <div className="mt-6 flex gap-3">
            <a href={oauthUrl('google')} className="flex-1 btn-outline text-center text-sm py-2">Google</a>
            <a href={oauthUrl('github')} className="flex-1 btn-outline text-center text-sm py-2">GitHub</a>
          </div>
          <p className="text-center text-sm text-muted mt-6">
            Have an account? <Link to={`/login?tab=${tab}`} className="text-accent">Login</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
