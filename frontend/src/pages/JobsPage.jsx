import { useCallback, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../api/client';
import { useJobActions } from '../hooks/useJobActions';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import JobCard from '../components/JobCard';
import JobFilters from '../components/JobFilters';

export default function JobsPage() {
  const [searchParams] = useSearchParams();
  const [jobs, setJobs] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState({
    keyword: searchParams.get('q') || '',
    location: '',
    company: '',
    skill: '',
    experience: '',
    minSalary: '',
    maxSalary: '',
    workMode: '',
    sort: 'latest',
  });

  const fetchJobs = useCallback(async () => {
    const params = new URLSearchParams({ page, size: 12, sort: filters.sort });
    if (filters.keyword) params.set('keyword', filters.keyword);
    if (filters.location) params.set('location', filters.location);
    if (filters.company) params.set('company', filters.company);
    if (filters.skill) params.set('skill', filters.skill);
    if (filters.experience) params.set('experience', filters.experience);
    if (filters.minSalary) params.set('minSalary', filters.minSalary);
    if (filters.maxSalary) params.set('maxSalary', filters.maxSalary);
    if (filters.workMode) params.set('workMode', filters.workMode);
    const res = await apiRequest(`/api/jobs?${params}`);
    setJobs(res.data.content || []);
    setTotalPages(res.data.totalPages || 0);
  }, [filters, page]);

  useEffect(() => {
    fetchJobs().catch((err) => {
      toast.error(err.message || 'Could not load jobs');
      setJobs([]);
    });
  }, [fetchJobs]);

  const { apply, save, messageRecruiter } = useJobActions(fetchJobs);

  return (
    <div className="min-h-screen">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="font-poppins text-3xl font-bold mb-8">Browse Jobs</h1>
        <div className="flex flex-col lg:flex-row gap-8">
          <div className="lg:w-72 shrink-0">
            <JobFilters filters={filters} onChange={(f) => { setFilters(f); setPage(0); }} />
            <button type="button" onClick={fetchJobs} className="btn-primary w-full mt-4">Search</button>
          </div>
          <div className="flex-1 space-y-4">
            {jobs.map((job) => (
              <JobCard
                key={job.id}
                job={job}
                onApply={apply}
                onSave={save}
                onMessage={messageRecruiter}
              />
            ))}
            {jobs.length === 0 && <p className="text-muted text-center py-12">No jobs found</p>}
            {totalPages > 1 && (
              <div className="flex justify-center gap-2 mt-6">
                <button type="button" disabled={page === 0} onClick={() => setPage(page - 1)} className="btn-outline text-sm">Prev</button>
                <span className="px-4 py-2 text-sm text-muted">Page {page + 1} of {totalPages}</span>
                <button type="button" disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)} className="btn-outline text-sm">Next</button>
              </div>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}
