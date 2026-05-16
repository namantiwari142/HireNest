import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { apiRequest } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

export default function ApplicantProfile() {
  const { updateUser } = useAuth();
  const [profile, setProfile] = useState({ skills: [] });
  const [skillsInput, setSkillsInput] = useState('');

  useEffect(() => {
    apiRequest('/api/applicant/profile').then((r) => {
      setProfile(r.data);
      setSkillsInput((r.data.skills || []).join(', '));
    });
  }, []);

  const save = async (e) => {
    e.preventDefault();
    try {
      const res = await apiRequest('/api/applicant/profile', {
        method: 'PUT',
        body: JSON.stringify({
          ...profile,
          skills: skillsInput.split(',').map((s) => s.trim()).filter(Boolean),
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
    const fd = new FormData();
    fd.append('file', file);
    const endpoint = type === 'avatar' ? '/api/applicant/profile/avatar' : '/api/applicant/profile/resume';
    try {
      const res = await apiRequest(endpoint, { method: 'POST', body: fd });
      setProfile(res.data);
      toast.success('Uploaded');
    } catch (err) {
      toast.error(err.message);
    }
  };

  return (
    <div>
      <h1 className="font-poppins text-2xl font-bold">My Profile</h1>
      <div className="card mt-6 flex items-center gap-6">
        <img src={profile.profileImageUrl || `https://ui-avatars.com/api/?name=${profile.name}`} alt="" className="w-20 h-20 rounded-full object-cover" />
        <div>
          <p className="font-semibold">{profile.name}</p>
          <p className="text-sm text-muted">{profile.email}</p>
          <p className="text-sm text-accent mt-1">{profile.profileCompletion}% complete</p>
          <label className="btn-outline text-xs mt-2 inline-block cursor-pointer">
            Upload photo
            <input type="file" accept="image/*" className="hidden" onChange={(e) => e.target.files[0] && upload('avatar', e.target.files[0])} />
          </label>
        </div>
      </div>
      <form onSubmit={save} className="card mt-6 space-y-4">
        <input className="input-field" placeholder="Phone" value={profile.phone || ''} onChange={(e) => setProfile({ ...profile, phone: e.target.value })} />
        <input className="input-field" placeholder="Location" value={profile.location || ''} onChange={(e) => setProfile({ ...profile, location: e.target.value })} />
        <textarea className="input-field min-h-[100px]" placeholder="Bio" value={profile.bio || ''} onChange={(e) => setProfile({ ...profile, bio: e.target.value })} />
        <input className="input-field" placeholder="Skills (comma separated)" value={skillsInput} onChange={(e) => setSkillsInput(e.target.value)} />
        <textarea className="input-field min-h-[80px] font-mono text-xs" placeholder='Education JSON e.g. [{"degree":"B.Tech","school":"XYZ"}]' value={profile.educationJson || '[]'} onChange={(e) => setProfile({ ...profile, educationJson: e.target.value })} />
        <textarea className="input-field min-h-[80px] font-mono text-xs" placeholder="Experience JSON" value={profile.experienceJson || '[]'} onChange={(e) => setProfile({ ...profile, experienceJson: e.target.value })} />
        <textarea className="input-field min-h-[80px] font-mono text-xs" placeholder="Projects JSON" value={profile.projectsJson || '[]'} onChange={(e) => setProfile({ ...profile, projectsJson: e.target.value })} />
        <label className="btn-outline inline-block cursor-pointer text-sm">
          Upload Resume (PDF)
          <input type="file" accept="application/pdf" className="hidden" onChange={(e) => e.target.files[0] && upload('resume', e.target.files[0])} />
        </label>
        {profile.resumeUrl && <a href={profile.resumeUrl} target="_blank" rel="noreferrer" className="text-accent text-sm block">View resume</a>}
        <button type="submit" className="btn-primary">Save Profile</button>
      </form>
    </div>
  );
}
