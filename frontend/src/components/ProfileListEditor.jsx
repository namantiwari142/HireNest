export default function ProfileListEditor({ title, description, items, fields, onChange, onAdd, onRemove, emptyLabel }) {
  return (
    <section className="card space-y-4">
      <div>
        <h2 className="font-semibold">{title}</h2>
        {description && <p className="text-xs text-muted mt-1">{description}</p>}
      </div>

      {items.map((item, index) => (
        <div key={index} className="p-4 rounded-lg bg-white/5 border border-white/10 space-y-3">
          <div className="flex items-center justify-between">
            <span className="text-xs text-muted uppercase tracking-wide">
              {title} {index + 1}
            </span>
            {items.length > 1 && (
              <button
                type="button"
                onClick={() => onRemove(index)}
                className="text-xs text-red-400 hover:text-red-300"
              >
                Remove
              </button>
            )}
          </div>
          {fields.map((field) => (
            <input
              key={field.key}
              className="input-field"
              placeholder={field.placeholder}
              value={item[field.key] || ''}
              onChange={(e) => onChange(index, field.key, e.target.value)}
            />
          ))}
        </div>
      ))}

      <button type="button" onClick={onAdd} className="btn-outline text-sm">
        + Add {emptyLabel || title}
      </button>
    </section>
  );
}
