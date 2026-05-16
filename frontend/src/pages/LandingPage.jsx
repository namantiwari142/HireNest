import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../api/client';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import JobCard from '../components/JobCard';

export default function LandingPage() {
  const { isAuthenticated, user } = useAuth();
  const [featured, setFeatured] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [skills, setSkills] = useState([]);

  const dashboardPath = user?.role === 'RECRUITER' ? '/recruiter/dashboard' : '/applicant/dashboard';

  const loadFeatured = () => {
    apiRequest('/api/jobs?page=0&size=12&sort=latest')
      .then((r) => setFeatured(r.data?.content || []))
      .catch(() => {
        apiRequest('/api/jobs/featured?limit=12')
          .then((res) => setFeatured(res.data || []))
          .catch(() => setFeatured([]));
      });
  };

  useEffect(() => {
    loadFeatured();
    apiRequest('/api/public/companies').then((r) => setCompanies(r.data || [])).catch(() => {});
    apiRequest('/api/public/skills').then((r) => setSkills(r.data || [])).catch(() => {});
  }, []);

  return (
    <div className="min-h-screen">
      <Navbar transparent />
      <section className="relative overflow-hidden pt-16 pb-24">
        <div className="absolute inset-0 bg-gradient-to-br from-accent/10 via-transparent to-transparent pointer-events-none" />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
          <div className="max-w-3xl">
            <span className="inline-block text-accent text-sm font-medium bg-accent/10 px-3 py-1 rounded-full mb-6">
              #1 Job Portal for Freshers
            </span>
            <h1 className="font-poppins text-4xl sm:text-5xl lg:text-6xl font-bold leading-tight">
              Find Your Dream Job with <span className="text-accent">HireNest</span>
            </h1>
            <p className="text-muted text-lg mt-6 max-w-xl">
              Connect with top companies, apply in one click, chat with recruiters in real time, and track your applications — all in one place.
            </p>
            <div className="flex flex-wrap gap-4 mt-8">
              <Link to="/jobs" className="btn-primary">Browse Jobs</Link>
              {isAuthenticated ? (
                <Link to={dashboardPath} className="btn-outline">Go to Dashboard</Link>
              ) : (
                <Link to="/login?tab=applicant" className="btn-outline">Get Started</Link>
              )}
            </div>
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <h2 className="font-poppins text-2xl font-bold mb-8">Featured Jobs</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {featured.map((job) => (
            <JobCard key={job.id} job={job} showActions={false} />
          ))}
        </div>
        <div className="text-center mt-8">
          <Link to="/jobs" className="btn-outline">View All Jobs</Link>
        </div>
      </section>

      <section className="bg-surface py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="font-poppins text-2xl font-bold mb-8">Top Companies</h2>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            {companies.map((c) => (
              <div key={c.name} className="card flex items-center gap-3 py-4">
                <img src={c.logo} alt={c.name} className="w-10 h-10 rounded-lg" />
                <span className="font-medium text-sm">{c.name}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <h2 className="font-poppins text-2xl font-bold mb-6">Trending Skills</h2>
        <div className="flex flex-wrap gap-3">
          {skills.map((s) => (
            <span key={s} className="px-4 py-2 bg-surface border border-white/10 rounded-full text-sm hover:border-accent/50 transition-colors">
              {s}
            </span>
          ))}
        </div>
      </section>

      <section className="bg-gradient-to-r from-accent/20 to-transparent py-16">
        <div className="max-w-3xl mx-auto text-center px-4">
          <h2 className="font-poppins text-3xl font-bold">Ready to start your journey?</h2>
          <p className="text-muted mt-4">Join thousands of professionals finding their perfect role on HireNest.</p>
          {isAuthenticated ? (
            <Link to={dashboardPath} className="btn-primary inline-block mt-8">Open Dashboard</Link>
          ) : (
            <Link to="/login?tab=applicant" className="btn-primary inline-block mt-8">Login to Get Started</Link>
          )}
        </div>
      </section>

      <Footer />
    </div>
  );
}
