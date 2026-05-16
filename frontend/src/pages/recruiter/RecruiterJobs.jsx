import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';
import { formatSalary } from '../../utils/format';

export default function RecruiterJobs() {
  const [jobs, setJobs] = useState([]);

  const load = () => apiRequest('/api/recruiter/jobs').then((r) => setJobs(r.data || []));
  useEffect(() => { load(); }, []);

  const remove = async (id) => {
    if (!confirm('Delete this job?')) return;
    await apiRequest(`/api/recruiter/jobs/${id}`, { method: 'DELETE' });
    toast.success('Job removed');
    load();
  };

  return (
    <div>
      <div className="flex justify-between items-center">
        <h1 className="font-poppins text-2xl font-bold">My Jobs</h1>
        <Link to="/recruiter/post-job" className="btn-primary text-sm">Post Job</Link>
      </div>
      <div className="mt-6 space-y-3">
        {jobs.map((j) => (
          <div key={j.id} className="card flex flex-col sm:flex-row justify-between gap-4">
            <div>
              <p className="font-semibold">{j.title}</p>
              <p className="text-sm text-muted">{j.location} · {formatSalary(j.salaryMin, j.salaryMax)}</p>
            </div>
            <div className="flex gap-2">
              <Link to={`/recruiter/jobs/${j.id}/applications`} className="btn-outline text-sm py-1.5">Applications</Link>
              <button type="button" onClick={() => remove(j.id)} className="text-sm text-red-400 px-3 py-1.5 border border-red-400/30 rounded-lg">Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
