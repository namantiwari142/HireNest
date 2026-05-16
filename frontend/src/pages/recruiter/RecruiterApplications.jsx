import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';

const STATUSES = ['APPLIED', 'SHORTLISTED', 'INTERVIEW', 'ACCEPTED', 'REJECTED'];

export default function RecruiterApplications() {
  const { jobId } = useParams();
  const [apps, setApps] = useState([]);

  const load = () => {
    const url = jobId ? `/api/recruiter/jobs/${jobId}/applications` : '/api/recruiter/applications';
    apiRequest(url).then((r) => setApps(r.data || []));
  };

  useEffect(() => { load(); }, [jobId]);

  const updateStatus = async (id, status) => {
    await apiRequest(`/api/recruiter/applications/${id}/status?status=${status}`, { method: 'PATCH' });
    toast.success('Status updated');
    load();
  };

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">Applications</h1>
      {jobId && <Link to="/recruiter/applications" className="text-sm text-accent">← All applications</Link>}
      <div className="mt-6 space-y-4">
        {apps.map((a) => (
          <div key={a.id} className="card">
            <div className="flex flex-col sm:flex-row justify-between gap-4">
              <div className="flex gap-4">
                <img src={a.profileImageUrl || `https://ui-avatars.com/api/?name=${a.applicantName}`} alt="" className="w-12 h-12 rounded-full" />
                <div>
                  <p className="font-semibold">{a.applicantName}</p>
                  <p className="text-sm text-muted">{a.jobTitle} · {a.applicantEmail}</p>
                  {a.resumeUrl && <a href={a.resumeUrl} target="_blank" rel="noreferrer" className="text-xs text-accent">Resume</a>}
                </div>
              </div>
              <select
                value={a.status}
                onChange={(e) => updateStatus(a.id, e.target.value)}
                className="input-field w-auto text-sm"
              >
                {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            {a.applicantId && (
              <Link to={`/recruiter/applicants/${a.applicantId}`} className="text-xs text-accent mt-2 inline-block">View profile</Link>
            )}
          </div>
        ))}
        {apps.length === 0 && <p className="text-muted">No applications yet.</p>}
      </div>
    </div>
  );
}
