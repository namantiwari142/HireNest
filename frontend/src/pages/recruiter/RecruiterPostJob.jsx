import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';

export default function RecruiterPostJob() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: '', description: '', salaryMin: '', salaryMax: '', experienceRequired: '',
    location: '', jobType: 'FULL_TIME', workMode: 'HYBRID', skills: '',
  });

  const submit = async (e) => {
    e.preventDefault();
    try {
      await apiRequest('/api/recruiter/jobs', {
        method: 'POST',
        body: JSON.stringify({
          ...form,
          salaryMin: form.salaryMin ? Number(form.salaryMin) : null,
          salaryMax: form.salaryMax ? Number(form.salaryMax) : null,
          skills: form.skills.split(',').map((s) => s.trim()).filter(Boolean),
        }),
      });
      toast.success('Job posted!');
      navigate('/recruiter/jobs');
    } catch (err) {
      toast.error(err.message);
    }
  };

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">Post a Job</h1>
      <form onSubmit={submit} className="card mt-6 space-y-4 max-w-2xl">
        <input className="input-field" placeholder="Job title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
        <textarea className="input-field min-h-[120px]" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required />
        <div className="grid sm:grid-cols-2 gap-4">
          <input className="input-field" type="number" placeholder="Min salary" value={form.salaryMin} onChange={(e) => setForm({ ...form, salaryMin: e.target.value })} />
          <input className="input-field" type="number" placeholder="Max salary" value={form.salaryMax} onChange={(e) => setForm({ ...form, salaryMax: e.target.value })} />
        </div>
        <input className="input-field" placeholder="Experience required" value={form.experienceRequired} onChange={(e) => setForm({ ...form, experienceRequired: e.target.value })} />
        <input className="input-field" placeholder="Location" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
        <input className="input-field" placeholder="Skills (comma separated)" value={form.skills} onChange={(e) => setForm({ ...form, skills: e.target.value })} />
        <select className="input-field" value={form.jobType} onChange={(e) => setForm({ ...form, jobType: e.target.value })}>
          <option value="FULL_TIME">Full Time</option>
          <option value="PART_TIME">Part Time</option>
          <option value="CONTRACT">Contract</option>
          <option value="INTERNSHIP">Internship</option>
        </select>
        <select className="input-field" value={form.workMode} onChange={(e) => setForm({ ...form, workMode: e.target.value })}>
          <option value="REMOTE">Remote</option>
          <option value="HYBRID">Hybrid</option>
          <option value="ONSITE">Onsite</option>
        </select>
        <button type="submit" className="btn-primary">Publish Job</button>
      </form>
    </div>
  );
}
