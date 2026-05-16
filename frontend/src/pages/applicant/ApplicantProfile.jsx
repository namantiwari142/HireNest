import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import ProfileListEditor from '../../components/ProfileListEditor';
import {
  EMPTY_EDUCATION,
  EMPTY_EXPERIENCE,
  EMPTY_PROJECT,
  parseProfileJson,
  toProfileJson,
  withDefaultRow,
} from '../../utils/profileSections';

const EDUCATION_FIELDS = [
  { key: 'degree', placeholder: 'Degree (e.g. B.Tech Computer Science)' },
  { key: 'school', placeholder: 'School / University' },
  { key: 'year', placeholder: 'Year (e.g. 2022–2026)' },
];

const EXPERIENCE_FIELDS = [
  { key: 'title', placeholder: 'Job title (e.g. Frontend Intern)' },
  { key: 'company', placeholder: 'Company name' },
  { key: 'duration', placeholder: 'Duration (e.g. Jun 2024 – Aug 2024)' },
];

const PROJECT_FIELDS = [
  { key: 'name', placeholder: 'Project name' },
  { key: 'description', placeholder: 'Short description' },
  { key: 'link', placeholder: 'Link (GitHub or live demo)' },
];

function updateListItem(list, setList, index, key, value) {
  setList(list.map((item, i) => (i === index ? { ...item, [key]: value } : item)));
}

export default function ApplicantProfile() {
  const { updateUser } = useAuth();
  const [profile, setProfile] = useState({ skills: [] });
  const [skillsInput, setSkillsInput] = useState('');
  const [education, setEducation] = useState([{ ...EMPTY_EDUCATION }]);
  const [experience, setExperience] = useState([{ ...EMPTY_EXPERIENCE }]);
  const [projects, setProjects] = useState([{ ...EMPTY_PROJECT }]);
  const [uploadingResume, setUploadingResume] = useState(false);

  const MAX_RESUME_BYTES = 5 * 1024 * 1024;

  useEffect(() => {
    apiRequest('/api/applicant/profile').then((r) => {
      const data = r.data;
      setProfile(data);
      setSkillsInput((data.skills || []).join(', '));
      setEducation(withDefaultRow(parseProfileJson(data.educationJson), EMPTY_EDUCATION));
      setExperience(withDefaultRow(parseProfileJson(data.experienceJson), EMPTY_EXPERIENCE));
      setProjects(withDefaultRow(parseProfileJson(data.projectsJson), EMPTY_PROJECT));
    });
  }, []);

  const save = async (e) => {
    e.preventDefault();
    try {
      const res = await apiRequest('/api/applicant/profile', {
        method: 'PUT',
        body: JSON.stringify({
          phone: profile.phone,
          location: profile.location,
          bio: profile.bio,
          skills: skillsInput.split(',').map((s) => s.trim()).filter(Boolean),
          educationJson: toProfileJson(education),
          experienceJson: toProfileJson(experience),
          projectsJson: toProfileJson(projects),
        }),
      });
      setProfile(res.data);
      updateUser({ profileImageUrl: res.data.profileImageUrl });
      toast.success('Profile updated');
    } catch (err) {
      toast.error(err.message);
    }
  };

  const upload = async (type, file) => {
    if (type === 'resume') {
      if (file.type && file.type !== 'application/pdf') {
        toast.error('Only PDF files are allowed');
        return;
      }
      if (!file.name.toLowerCase().endsWith('.pdf')) {
        toast.error('Resume must be a .pdf file');
        return;
      }
      if (file.size > MAX_RESUME_BYTES) {
        toast.error('Resume must be 5MB or smaller');
        return;
      }
    }

    const fd = new FormData();
    fd.append('file', file);
    const endpoint = type === 'avatar' ? '/api/applicant/profile/avatar' : '/api/applicant/profile/resume';
    const setLoading = type === 'resume' ? setUploadingResume : () => {};

    try {
      setLoading(true);
      const res = await apiRequest(endpoint, { method: 'POST', body: fd });
      setProfile(res.data);
      toast.success(type === 'resume' ? 'Resume uploaded' : 'Photo uploaded');
    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <h1 className="font-poppins text-2xl font-bold">My Profile</h1>

      <div className="card flex items-center gap-6">
        <img
          src={profile.profileImageUrl || `https://ui-avatars.com/api/?name=${profile.name}`}
          alt=""
          className="w-20 h-20 rounded-full object-cover"
        />
        <div>
          <p className="font-semibold">{profile.name}</p>
          <p className="text-sm text-muted">{profile.email}</p>
          <p className="text-sm text-accent mt-1">{profile.profileCompletion}% complete</p>
          <label className="btn-outline text-xs mt-2 inline-block cursor-pointer">
            Upload photo
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => e.target.files[0] && upload('avatar', e.target.files[0])}
            />
          </label>
        </div>
      </div>

      <form onSubmit={save} className="space-y-6">
        <section className="card space-y-4">
          <h2 className="font-semibold">Basic info</h2>
          <input
            className="input-field"
            placeholder="Phone"
            value={profile.phone || ''}
            onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
          />
          <input
            className="input-field"
            placeholder="Location (city, country)"
            value={profile.location || ''}
            onChange={(e) => setProfile({ ...profile, location: e.target.value })}
          />
          <textarea
            className="input-field min-h-[100px]"
            placeholder="Short bio — tell recruiters about yourself"
            value={profile.bio || ''}
            onChange={(e) => setProfile({ ...profile, bio: e.target.value })}
          />
          <input
            className="input-field"
            placeholder="Skills (comma separated, e.g. React, Java, SQL)"
            value={skillsInput}
            onChange={(e) => setSkillsInput(e.target.value)}
          />
        </section>

        <ProfileListEditor
          title="Education"
          description="Add your degrees and schools"
          items={education}
          fields={EDUCATION_FIELDS}
          emptyLabel="education"
          onChange={(index, key, value) => updateListItem(education, setEducation, index, key, value)}
          onAdd={() => setEducation([...education, { ...EMPTY_EDUCATION }])}
          onRemove={(index) => setEducation(education.filter((_, i) => i !== index))}
        />

        <ProfileListEditor
          title="Experience"
          description="Internships, jobs, or relevant work"
          items={experience}
          fields={EXPERIENCE_FIELDS}
          emptyLabel="experience"
          onChange={(index, key, value) => updateListItem(experience, setExperience, index, key, value)}
          onAdd={() => setExperience([...experience, { ...EMPTY_EXPERIENCE }])}
          onRemove={(index) => setExperience(experience.filter((_, i) => i !== index))}
        />

        <ProfileListEditor
          title="Projects"
          description="Portfolio projects recruiters can review"
          items={projects}
          fields={PROJECT_FIELDS}
          emptyLabel="project"
          onChange={(index, key, value) => updateListItem(projects, setProjects, index, key, value)}
          onAdd={() => setProjects([...projects, { ...EMPTY_PROJECT }])}
          onRemove={(index) => setProjects(projects.filter((_, i) => i !== index))}
        />

        <section className="card space-y-3">
          <h2 className="font-semibold">Resume</h2>
          <p className="text-xs text-muted">Upload a PDF resume (max 5MB)</p>
          <label
            className={`btn-outline inline-block text-sm ${uploadingResume ? 'opacity-60 pointer-events-none' : 'cursor-pointer'}`}
          >
            {uploadingResume ? 'Uploading…' : 'Upload resume (PDF)'}
            <input
              type="file"
              accept="application/pdf,.pdf"
              className="hidden"
              disabled={uploadingResume}
              onChange={(e) => {
                const file = e.target.files?.[0];
                if (file) upload('resume', file);
                e.target.value = '';
              }}
            />
          </label>
          {profile.resumeUrl && (
            <a
              href={profile.resumeUrl}
              target="_blank"
              rel="noreferrer"
              className="text-accent text-sm block hover:underline"
            >
              View uploaded resume
            </a>
          )}
        </section>

        <button type="submit" className="btn-primary w-full sm:w-auto">
          Save profile
        </button>
      </form>
    </div>
  );
}
