export default function StatsCard({ label, value, icon }) {
  return (
    <div className="card flex items-center gap-4">
      <div className="w-12 h-12 rounded-xl bg-accent/10 flex items-center justify-center text-2xl">{icon}</div>
      <div>
        <p className="text-2xl font-poppins font-bold">{value}</p>
        <p className="text-sm text-muted">{label}</p>
      </div>
    </div>
  );
}
