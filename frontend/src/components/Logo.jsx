import { Link } from 'react-router-dom';

export default function Logo({ className = '' }) {
  return (
    <Link to="/" className={`flex items-center gap-2 font-poppins font-bold text-xl ${className}`}>
      <span className="w-9 h-9 rounded-lg bg-accent flex items-center justify-center text-background text-lg">H</span>
      <span>Hire<span className="text-accent">Nest</span></span>
    </Link>
  );
}
