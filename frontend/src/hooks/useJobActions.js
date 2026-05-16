import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { apiRequest } from '../api/client';
import { useAuth } from '../context/AuthContext';

export function useJobActions(onSuccess) {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const isApplicant = isAuthenticated && user?.role === 'APPLICANT';

  const requireApplicant = () => {
    if (!isAuthenticated) {
      toast.error('Please login as an applicant');
      navigate('/login?tab=applicant');
      return false;
    }
    if (user?.role !== 'APPLICANT') {
      toast.error('Only applicants can apply, save, or message from jobs');
      return false;
    }
    return true;
  };

  const apply = async (jobId) => {
    if (!requireApplicant()) return false;
    try {
      await apiRequest(`/api/applicant/jobs/${jobId}/apply`, { method: 'POST' });
      toast.success('Applied successfully!');
      await onSuccess?.();
      return true;
    } catch (e) {
      toast.error(e.message || 'Failed to apply');
      return false;
    }
  };

  const save = async (jobId) => {
    if (!requireApplicant()) return false;
    try {
      await apiRequest(`/api/applicant/jobs/${jobId}/save`, { method: 'POST' });
      toast.success('Saved to your list');
      await onSuccess?.();
      return true;
    } catch (e) {
      toast.error(e.message || 'Failed to save job');
      return false;
    }
  };

  const messageRecruiter = (job) => {
    if (!requireApplicant()) return;
    if (!job?.recruiterUserId) {
      toast.error('Recruiter contact is not available for this job');
      return;
    }
    navigate('/chat', {
      state: {
        userId: job.recruiterUserId,
        name: job.recruiterName || job.companyName || 'Recruiter',
        profileImageUrl: job.companyLogo || null,
      },
    });
  };

  return { apply, save, messageRecruiter, isApplicant };
}
