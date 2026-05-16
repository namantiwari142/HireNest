export function formatSalary(min, max) {
  if (!min && !max) return 'Not disclosed';
  const fmt = (n) => {
    if (n >= 100000) return `₹${(n / 100000).toFixed(1)}L`;
    return `₹${n.toLocaleString('en-IN')}`;
  };
  if (min && max) return `${fmt(min)} - ${fmt(max)}`;
  return fmt(min || max);
}

export function formatDate(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

export function timeAgo(iso) {
  const diff = Date.now() - new Date(iso).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `${hrs}h ago`;
  return formatDate(iso);
}
