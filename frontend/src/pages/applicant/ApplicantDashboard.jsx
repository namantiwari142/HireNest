import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';
import StatsCard from '../../components/StatsCard';

export default function ApplicantDashboard() {
  const [stats, setStats] = useState({});
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [statsRes, appsRes] = await Promise.all([
        apiRequest('/api/applicant/dashboard'),
        apiRequest('/api/applicant/applications'),
      ]);
      setStats(statsRes.data || {});
      setApplications((appsRes.data || []).slice(0, 5));
    } catch (e) {
      toast.error(e.message || 'Could not load dashboard');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load, location.pathname]);

  if (loading) {
    return <p className="text-muted">Loading dashboard...</p>;
  }

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">Dashboard</h1>
      <p className="text-muted text-sm mt-1">Track your job search progress</p>
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-8">
        <StatsCard label="Applications" value={stats.totalApplications ?? 0} icon="📋" />
        <StatsCard label="Saved Jobs" value={stats.savedJobs ?? 0} icon="⭐" />
        <StatsCard label="Notifications" value={stats.unreadNotifications ?? 0} icon="🔔" />
        <StatsCard label="Profile" value={`${stats.profileCompletion ?? 0}%`} icon="👤" />
      </div>
      <div className="mt-10">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-poppins font-semibold">Recent Applications</h2>
          <Link to="/applicant/applications" className="text-sm text-accent hover:underline">View all</Link>
        </div>
        <div className="space-y-3">
          {applications.map((a) => (
            <div key={a.id} className="card flex justify-between items-center py-4">
              <div>
                <p className="font-medium">{a.jobTitle}</p>
                <p className="text-sm text-muted">{a.companyName}</p>
              </div>
              <span className="text-xs bg-accent/10 text-accent px-3 py-1 rounded-full">{a.status}</span>
            </div>
          ))}
          {applications.length === 0 && (
            <p className="text-muted text-sm">
              No applications yet. <Link to="/jobs" className="text-accent">Browse jobs</Link>
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
