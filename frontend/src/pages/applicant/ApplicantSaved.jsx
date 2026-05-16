import { useCallback, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';
import JobCard from '../../components/JobCard';
import { useJobActions } from '../../hooks/useJobActions';

export default function ApplicantSaved() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiRequest('/api/applicant/saved-jobs');
      setJobs(res.data || []);
    } catch (e) {
      toast.error(e.message || 'Could not load saved jobs');
      setJobs([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const { save } = useJobActions(load);

  useEffect(() => {
    load();
  }, [load, location.pathname]);

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">Saved Jobs</h1>
      <p className="text-muted text-sm mt-1">{jobs.length} saved job(s)</p>
      {loading ? (
        <p className="text-muted mt-8">Loading saved jobs...</p>
      ) : (
        <div className="mt-6 space-y-4">
          {jobs.map((job) => (
            <JobCard key={job.id} job={{ ...job, saved: true }} onSave={save} showActions />
          ))}
          {jobs.length === 0 && (
            <p className="text-muted">
              No saved jobs yet. Browse jobs and tap Save to add them here.
            </p>
          )}
        </div>
      )}
    </div>
  );
}
