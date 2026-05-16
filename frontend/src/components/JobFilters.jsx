export default function JobFilters({ filters, onChange }) {
  const update = (key, value) => onChange({ ...filters, [key]: value });

  return (
    <div className="card space-y-4">
      <h3 className="font-poppins font-semibold">Filters</h3>
      <input
        type="text"
        placeholder="Keyword"
        value={filters.keyword || ''}
        onChange={(e) => update('keyword', e.target.value)}
        className="input-field"
      />
      <input
        type="text"
        placeholder="Location"
        value={filters.location || ''}
        onChange={(e) => update('location', e.target.value)}
        className="input-field"
      />
      <input
        type="text"
        placeholder="Company"
        value={filters.company || ''}
        onChange={(e) => update('company', e.target.value)}
        className="input-field"
      />
      <input
        type="text"
        placeholder="Skill"
        value={filters.skill || ''}
        onChange={(e) => update('skill', e.target.value)}
        className="input-field"
      />
      <input
        type="text"
        placeholder="Experience"
        value={filters.experience || ''}
        onChange={(e) => update('experience', e.target.value)}
        className="input-field"
      />
      <div className="grid grid-cols-2 gap-2">
        <input
          type="number"
          placeholder="Min salary"
          value={filters.minSalary || ''}
          onChange={(e) => update('minSalary', e.target.value)}
          className="input-field"
        />
        <input
          type="number"
          placeholder="Max salary"
          value={filters.maxSalary || ''}
          onChange={(e) => update('maxSalary', e.target.value)}
          className="input-field"
        />
      </div>
      <select
        value={filters.workMode || ''}
        onChange={(e) => update('workMode', e.target.value)}
        className="input-field"
      >
        <option value="">Work mode</option>
        <option value="REMOTE">Remote</option>
        <option value="HYBRID">Hybrid</option>
        <option value="ONSITE">Onsite</option>
      </select>
      <select
        value={filters.sort || 'latest'}
        onChange={(e) => update('sort', e.target.value)}
        className="input-field"
      >
        <option value="latest">Latest</option>
        <option value="salary_desc">Salary: High to Low</option>
        <option value="salary_asc">Salary: Low to High</option>
        <option value="experience">Experience</option>
      </select>
    </div>
  );
}
