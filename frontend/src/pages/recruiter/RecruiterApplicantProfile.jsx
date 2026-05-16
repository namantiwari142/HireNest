import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';
import ProfileSectionsView from '../../components/ProfileSectionsView';

export default function RecruiterApplicantProfile() {
  const { applicantId } = useParams();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!applicantId) return;
    setLoading(true);
    setError(null);
    apiRequest(`/api/recruiter/applicants/${applicantId}`)
      .then((r) => setProfile(r.data))
      .catch((err) => {
        setError(err.message);
        toast.error(err.message);
      })
      .finally(() => setLoading(false));
  }, [applicantId]);

  if (loading) {
    return <p className="text-muted">Loading applicant profile…</p>;
  }

  if (error || !profile) {
    return (
      <div>
        <Link to="/recruiter/applications" className="text-sm text-accent hover:underline">
          ← Back to applications
        </Link>
        <p className="text-muted mt-6">{error || 'Applicant not found.'}</p>
      </div>
    );
  }

  return (
    <div>
      <Link to="/recruiter/applications" className="text-sm text-accent hover:underline">
        ← Back to applications
      </Link>

      <div className="card mt-6 flex flex-col sm:flex-row gap-6 items-start">
        <img
          src={profile.profileImageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile.name || 'A')}`}
          alt=""
          className="w-20 h-20 rounded-full object-cover"
        />
        <div className="flex-1">
          <h1 className="font-poppins text-2xl font-bold">{profile.name}</h1>
          <p className="text-muted text-sm mt-1">{profile.email}</p>
          {profile.phone && <p className="text-sm mt-2">Phone: {profile.phone}</p>}
          {profile.location && <p className="text-sm">Location: {profile.location}</p>}
          <p className="text-sm text-accent mt-2">Profile {profile.profileCompletion}% complete</p>
          {profile.resumeUrl && (
            <a
              href={profile.resumeUrl}
              target="_blank"
              rel="noreferrer"
              className="btn-outline text-sm mt-4 inline-block"
            >
              Download resume (PDF)
            </a>
          )}
        </div>
      </div>

      {profile.bio ? (
        <section className="card mt-6">
          <h2 className="font-semibold mb-2">Bio</h2>
          <p className="text-sm text-muted whitespace-pre-wrap">{profile.bio}</p>
        </section>
      ) : null}

      {profile.skills?.length > 0 && (
        <section className="card mt-6">
          <h2 className="font-semibold mb-3">Skills</h2>
          <div className="flex flex-wrap gap-2">
            {profile.skills.map((skill) => (
              <span key={skill} className="px-3 py-1 rounded-full bg-white/5 text-sm">
                {skill}
              </span>
            ))}
          </div>
        </section>
      )}

      <ProfileSectionsView profile={profile} />
    </div>
  );
}
