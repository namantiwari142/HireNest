import { parseProfileJson } from '../utils/profileSections';

function ListSection({ title, items, renderItem }) {
  return (
    <section className="card mt-6">
      <h2 className="font-semibold mb-3">{title}</h2>
      {items.length === 0 ? (
        <p className="text-sm text-muted">Not provided</p>
      ) : (
        <ul className="space-y-3 text-sm">
          {items.map((item, i) => (
            <li key={i} className="p-3 rounded-lg bg-white/5 border border-white/10">
              {renderItem(item)}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

export default function ProfileSectionsView({ profile }) {
  const education = parseProfileJson(profile.educationJson);
  const experience = parseProfileJson(profile.experienceJson);
  const projects = parseProfileJson(profile.projectsJson);

  return (
    <>
      <ListSection
        title="Education"
        items={education}
        renderItem={(item) => (
          <>
            <p className="font-medium">{item.degree || 'Degree not specified'}</p>
            {item.school && <p className="text-muted">{item.school}</p>}
            {item.year && <p className="text-xs text-muted mt-1">{item.year}</p>}
          </>
        )}
      />
      <ListSection
        title="Experience"
        items={experience}
        renderItem={(item) => (
          <>
            <p className="font-medium">{item.title || 'Role not specified'}</p>
            {item.company && <p className="text-muted">{item.company}</p>}
            {item.duration && <p className="text-xs text-muted mt-1">{item.duration}</p>}
          </>
        )}
      />
      <ListSection
        title="Projects"
        items={projects}
        renderItem={(item) => (
          <>
            <p className="font-medium">{item.name || item.title || 'Project'}</p>
            {item.description && <p className="text-muted mt-1">{item.description}</p>}
            {item.link && (
              <a href={item.link} target="_blank" rel="noreferrer" className="text-accent text-xs mt-2 inline-block hover:underline">
                View project
              </a>
            )}
          </>
        )}
      />
    </>
  );
}
