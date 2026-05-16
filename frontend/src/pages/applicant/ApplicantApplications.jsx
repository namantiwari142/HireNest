import { useCallback, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';

const STATUS_COLORS = {
  APPLIED: 'bg-blue-500/20 text-blue-300',
  SHORTLISTED: 'bg-accent/20 text-accent',
  INTERVIEW: 'bg-purple-500/20 text-purple-300',
  ACCEPTED: 'bg-green-500/20 text-green-300',
  REJECTED: 'bg-red-500/20 text-red-400',
};

export default function ApplicantApplications() {
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiRequest('/api/applicant/applications');
      setApps(res.data || []);
    } catch (e) {
      toast.error(e.message || 'Could not load applications');
      setApps([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load, location.pathname]);

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">My Applications</h1>
      {loading ? (
        <p className="text-muted mt-6">Loading...</p>
      ) : (
        <div className="mt-6 space-y-3">
          {apps.map((a) => (
            <div key={a.id} className="card flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <p className="font-semibold">{a.jobTitle}</p>
                <p className="text-sm text-muted">{a.companyName}</p>
                <p className="text-xs text-muted mt-1">
                  Applied {new Date(a.appliedAt).toLocaleDateString()}
                </p>
              </div>
              <span className={`text-xs px-3 py-1 rounded-full w-fit ${STATUS_COLORS[a.status] || ''}`}>
                {a.status}
              </span>
            </div>
          ))}
          {apps.length === 0 && <p className="text-muted">No applications yet.</p>}
        </div>
      )}
    </div>
  );
}
