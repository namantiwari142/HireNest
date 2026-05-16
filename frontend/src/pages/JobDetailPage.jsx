import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../api/client';
import { useJobActions } from '../hooks/useJobActions';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { formatDate, formatSalary } from '../utils/format';

export default function JobDetailPage() {
  const { id } = useParams();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadJob = async () => {
    try {
      const res = await apiRequest(`/api/jobs/${id}`);
      setJob(res.data);
    } catch {
      toast.error('Job not found');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setLoading(true);
    loadJob();
  }, [id]);

  const { apply, save, messageRecruiter } = useJobActions(loadJob);

  if (loading) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <p className="text-center text-muted py-20">Loading job...</p>
      </div>
    );
  }

  if (!job) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <p className="text-center py-20"><Link to="/jobs" className="text-accent">Back to jobs</Link></p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="max-w-4xl mx-auto px-4 sm:px-6 py-8">
        <Link to="/jobs" className="text-sm text-muted hover:text-accent mb-6 inline-block">← Back to jobs</Link>

        <div className="card">
          <div className="flex flex-col sm:flex-row gap-6">
            <img
              src={job.companyLogo || `https://ui-avatars.com/api/?name=${job.companyName?.[0]}&background=F59E0B&color=121212`}
              alt={job.companyName}
              className="w-20 h-20 rounded-xl object-cover"
            />
            <div className="flex-1">
              <h1 className="font-poppins text-2xl sm:text-3xl font-bold">{job.title}</h1>
              <p className="text-accent font-medium mt-1">{job.companyName}</p>
              <div className="flex flex-wrap gap-2 mt-4 text-sm text-muted">
                <span className="bg-background px-3 py-1 rounded-lg">{formatSalary(job.salaryMin, job.salaryMax)}</span>
                <span className="bg-background px-3 py-1 rounded-lg">{job.location}</span>
                <span className="bg-background px-3 py-1 rounded-lg">{job.experienceRequired}</span>
                <span className="bg-background px-3 py-1 rounded-lg">{job.workMode}</span>
              </div>
            </div>
          </div>

          <div className="flex flex-wrap gap-3 mt-8 pt-6 border-t border-white/5">
            {!job.applied && (
              <button type="button" onClick={() => apply(job.id)} className="btn-primary">Apply Now</button>
            )}
            {job.applied && <span className="btn-outline cursor-default opacity-80">Already Applied</span>}
            <button
              type="button"
              onClick={() => save(job.id)}
              className={`btn-outline ${job.saved ? 'border-accent text-accent' : ''}`}
            >
              {job.saved ? 'Saved ✓' : 'Save Job'}
            </button>
            <button type="button" onClick={() => messageRecruiter(job)} className="btn-outline">
              Message Recruiter
            </button>
          </div>
        </div>

        <div className="card mt-6">
          <h2 className="font-poppins text-lg font-semibold mb-3">Job Description</h2>
          <p className="text-muted leading-relaxed whitespace-pre-wrap">{job.description}</p>
          <h3 className="font-poppins font-semibold mt-6 mb-2">Required Skills</h3>
          <div className="flex flex-wrap gap-2">
            {job.skills?.map((s) => (
              <span key={s} className="text-sm bg-accent/10 text-accent px-3 py-1 rounded-full">{s}</span>
            ))}
          </div>
          <p className="text-xs text-muted mt-6">Posted {formatDate(job.postedAt)}</p>
        </div>

        <div className="card mt-6">
          <h2 className="font-poppins text-lg font-semibold mb-3">About {job.companyName}</h2>
          <p className="text-muted leading-relaxed">
            {job.companyDescription || 'No company description provided yet.'}
          </p>
          {job.recruiterName && (
            <p className="text-sm text-muted mt-4">
              Posted by <span className="text-white">{job.recruiterName}</span>
            </p>
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
}
