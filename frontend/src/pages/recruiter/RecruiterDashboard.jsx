import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../../api/client';
import StatsCard from '../../components/StatsCard';

export default function RecruiterDashboard() {
  const [stats, setStats] = useState({});
  const [applications, setApplications] = useState([]);

  useEffect(() => {
    apiRequest('/api/recruiter/dashboard').then((r) => setStats(r.data || {}));
    apiRequest('/api/recruiter/applications').then((r) => setApplications((r.data || []).slice(0, 5)));
  }, []);

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">Recruiter Dashboard</h1>
      <div className="grid sm:grid-cols-3 gap-4 mt-8">
        <StatsCard label="Jobs Posted" value={stats.totalJobs ?? 0} icon="💼" />
        <StatsCard label="Total Applicants" value={stats.totalApplications ?? 0} icon="👥" />
        <StatsCard label="Notifications" value={stats.unreadNotifications ?? 0} icon="🔔" />
      </div>
      <div className="mt-10">
        <div className="flex justify-between mb-4">
          <h2 className="font-poppins font-semibold">Recent Applications</h2>
          <Link to="/recruiter/applications" className="text-sm text-accent">View all</Link>
        </div>
        <div className="space-y-3">
          {applications.map((a) => (
            <div key={a.id} className="card flex justify-between items-center py-4">
              <div>
                <p className="font-medium">{a.applicantName}</p>
                <p className="text-sm text-muted">{a.jobTitle}</p>
              </div>
              <span className="text-xs bg-accent/10 text-accent px-3 py-1 rounded-full">{a.status}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
