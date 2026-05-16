export const EMPTY_EDUCATION = { degree: '', school: '', year: '' };
export const EMPTY_EXPERIENCE = { title: '', company: '', duration: '' };
export const EMPTY_PROJECT = { name: '', description: '', link: '' };

export function parseProfileJson(value) {
  if (!value || String(value).trim() === '' || String(value).trim() === '[]') {
    return [];
  }
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export function toProfileJson(items) {
  const cleaned = items.filter((item) =>
    Object.values(item).some((v) => v != null && String(v).trim() !== '')
  );
  return JSON.stringify(cleaned);
}

export function withDefaultRow(items, emptyTemplate) {
  return items.length > 0 ? items : [{ ...emptyTemplate }];
}
