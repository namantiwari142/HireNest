import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

export default function AboutPage() {
  return (
    <div className="min-h-screen">
      <Navbar />
      <div className="max-w-3xl mx-auto px-4 py-20">
        <h1 className="font-poppins text-4xl font-bold">About HireNest</h1>
        <p className="text-muted mt-6 leading-relaxed">
          HireNest is a modern full-stack job portal built for fresher SDE portfolios. It demonstrates JWT authentication,
          OAuth2 login, role-based access control, real-time chat via WebSocket/STOMP, and a polished dark-themed UI.
        </p>
        <h2 className="font-poppins text-xl font-semibold mt-10">Tech Stack</h2>
        <ul className="mt-4 space-y-2 text-muted">
          <li>Frontend: React, Vite, Tailwind CSS, Context API</li>
          <li>Backend: Spring Boot, Spring Security, JPA, WebSocket</li>
          <li>Database: MySQL</li>
          <li>File uploads: Cloudinary</li>
        </ul>
      </div>
      <Footer />
    </div>
  );
}
