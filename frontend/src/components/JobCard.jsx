import { Link } from 'react-router-dom';
import { formatDate, formatSalary } from '../utils/format';

export default function JobCard({ job, onApply, onSave, onMessage, showActions = true }) {
  return (
    <div className="card group hover:border-accent/20 transition-colors">
      <Link to={`/jobs/${job.id}`} className="block">
        <div className="flex items-start gap-4">
          <img
            src={job.companyLogo || `https://ui-avatars.com/api/?name=${job.companyName?.[0]}&background=F59E0B&color=121212`}
            alt={job.companyName}
            className="w-12 h-12 rounded-lg object-cover"
          />
          <div className="flex-1 min-w-0">
            <h3 className="font-poppins font-semibold text-lg group-hover:text-accent transition-colors">
              {job.title}
            </h3>
            <p className="text-muted text-sm mt-0.5">{job.companyName}</p>
            <p className="text-muted text-sm mt-2 line-clamp-2">{job.description}</p>
          </div>
        </div>
        <div className="mt-4 flex flex-wrap gap-2 text-sm text-muted">
          <span className="bg-background px-2 py-1 rounded">{formatSalary(job.salaryMin, job.salaryMax)}</span>
          <span className="bg-background px-2 py-1 rounded">{job.location}</span>
          <span className="bg-background px-2 py-1 rounded">{job.experienceRequired}</span>
          <span className="bg-background px-2 py-1 rounded">{job.workMode}</span>
        </div>
        <div className="mt-3 flex flex-wrap gap-1.5">
          {job.skills?.slice(0, 4).map((s) => (
            <span key={s} className="text-xs bg-accent/10 text-accent px-2 py-0.5 rounded-full">{s}</span>
          ))}
        </div>
      </Link>
      <div className="mt-4 flex items-center justify-between flex-wrap gap-2">
        <span className="text-xs text-muted">Posted {formatDate(job.postedAt)}</span>
        {showActions && (
          <div className="flex gap-2 flex-wrap" onClick={(e) => e.preventDefault()}>
            <Link to={`/jobs/${job.id}`} className="text-sm text-accent hover:underline px-2 py-1.5">
              View details
            </Link>
            {onSave && (
              <button
                type="button"
                onClick={() => onSave(job.id)}
                className={`text-sm px-3 py-1.5 rounded-lg border transition-colors ${
                  job.saved ? 'border-accent text-accent bg-accent/10' : 'border-white/10 hover:border-accent/50'
                }`}
              >
                {job.saved ? 'Saved' : 'Save'}
              </button>
            )}
            {onMessage && (
              <button type="button" onClick={() => onMessage(job)} className="text-sm btn-outline py-1.5 px-3">
                Message
              </button>
            )}
            {onApply && !job.applied && (
              <button type="button" onClick={() => onApply(job.id)} className="btn-primary text-sm py-1.5 px-4">
                Apply
              </button>
            )}
            {job.applied && <span className="text-sm text-accent font-medium px-2">Applied</span>}
          </div>
        )}
      </div>
    </div>
  );
}
