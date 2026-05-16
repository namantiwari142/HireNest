import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './routes/ProtectedRoute';
import GuestRoute from './routes/GuestRoute';
import DashboardLayout from './layouts/DashboardLayout';
import { useAuth } from './context/AuthContext';

import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AboutPage from './pages/AboutPage';
import OAuthCallbackPage from './pages/OAuthCallbackPage';
import JobsPage from './pages/JobsPage';
import JobDetailPage from './pages/JobDetailPage';
import ChatPage from './pages/ChatPage';

import ApplicantDashboard from './pages/applicant/ApplicantDashboard';
import ApplicantApplications from './pages/applicant/ApplicantApplications';
import ApplicantSaved from './pages/applicant/ApplicantSaved';
import ApplicantProfile from './pages/applicant/ApplicantProfile';

import RecruiterDashboard from './pages/recruiter/RecruiterDashboard';
import RecruiterJobs from './pages/recruiter/RecruiterJobs';
import RecruiterPostJob from './pages/recruiter/RecruiterPostJob';
import RecruiterApplications from './pages/recruiter/RecruiterApplications';
import RecruiterApplicantProfile from './pages/recruiter/RecruiterApplicantProfile';

const applicantLinks = [
  { to: '/applicant/dashboard', label: 'Dashboard', icon: '📊', end: true },
  { to: '/jobs', label: 'Browse Jobs', icon: '🔍' },
  { to: '/applicant/applications', label: 'Applications', icon: '📋', end: true },
  { to: '/applicant/saved', label: 'Saved Jobs', icon: '⭐', end: true },
  { to: '/applicant/profile', label: 'Profile', icon: '👤', end: true },
  { to: '/chat', label: 'Messages', icon: '💬' },
];

const recruiterLinks = [
  { to: '/recruiter/dashboard', label: 'Dashboard', icon: '📊', end: true },
  { to: '/recruiter/jobs', label: 'My Jobs', icon: '💼', end: true },
  { to: '/recruiter/post-job', label: 'Post Job', icon: '➕', end: true },
  { to: '/recruiter/applications', label: 'Applications', icon: '📋', end: true },
  { to: '/chat', label: 'Messages', icon: '💬' },
];

function AppRoutes() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center text-muted">
        Loading...
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<GuestRoute><LoginPage /></GuestRoute>} />
      <Route path="/register" element={<GuestRoute><RegisterPage /></GuestRoute>} />
      <Route path="/about" element={<AboutPage />} />
      <Route path="/oauth/callback" element={<OAuthCallbackPage />} />
      <Route path="/jobs" element={<JobsPage />} />
      <Route path="/jobs/:id" element={<JobDetailPage />} />

      <Route path="/chat" element={<ProtectedRoute roles={['APPLICANT', 'RECRUITER', 'ADMIN']}><ChatPage /></ProtectedRoute>} />

      <Route path="/applicant" element={<ProtectedRoute roles={['APPLICANT']}><DashboardLayout links={applicantLinks} /></ProtectedRoute>}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<ApplicantDashboard />} />
        <Route path="applications" element={<ApplicantApplications />} />
        <Route path="saved" element={<ApplicantSaved />} />
        <Route path="profile" element={<ApplicantProfile />} />
      </Route>

      <Route path="/recruiter" element={<ProtectedRoute roles={['RECRUITER']}><DashboardLayout links={recruiterLinks} /></ProtectedRoute>}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<RecruiterDashboard />} />
        <Route path="jobs" element={<RecruiterJobs />} />
        <Route path="post-job" element={<RecruiterPostJob />} />
        <Route path="applications" element={<RecruiterApplications />} />
        <Route path="jobs/:jobId/applications" element={<RecruiterApplications />} />
        <Route path="applicants/:applicantId" element={<RecruiterApplicantProfile />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return <AppRoutes />;
}
