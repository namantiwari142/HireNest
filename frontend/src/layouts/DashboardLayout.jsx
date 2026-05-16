import { NavLink, Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';

export default function DashboardLayout({ links }) {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex flex-col lg:flex-row gap-8">
        <aside className="lg:w-64 shrink-0">
          <nav className="card p-3 space-y-1 sticky top-24">
            {links.map(({ to, label, icon, end }) => (
              <NavLink
                key={to}
                to={to}
                end={end}
                className={({ isActive }) => (isActive ? 'sidebar-link-active' : 'sidebar-link')}
              >
                <span>{icon}</span>
                {label}
              </NavLink>
            ))}
          </nav>
        </aside>
        <main className="flex-1 min-w-0">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
