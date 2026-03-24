export function requirePhoneOrFacebook(phoneNumber, facebookUrl) {
  const hasPhone = !!phoneNumber?.trim();
  const hasFacebook = !!facebookUrl?.trim();
  return hasPhone || hasFacebook;
}

function normalizePhoneValue(value) {
  if (!value) {
    return null;
  }
  const cleaned = value.replace(/[\s.-]/g, '').trim();
  return cleaned || null;
}

function normalizeFacebookUrl(value) {
  if (!value) {
    return null;
  }
  const trimmed = value.trim();
  if (!trimmed) {
    return null;
  }
  if (/^https?:\/\//i.test(trimmed)) {
    return trimmed;
  }
  return `https://${trimmed}`;
}

export function normalizeProfilePayload(form) {
  return {
    phoneNumber: normalizePhoneValue(form.phoneNumber),
    facebookUrl: normalizeFacebookUrl(form.facebookUrl),
    parentPhone: normalizePhoneValue(form.parentPhone),
    address: form.address?.trim() || null,
  };
}
