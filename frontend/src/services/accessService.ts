import { AppRole } from '../types/app';
import { getAuthUser } from '../utils/storage';

const ROLE_CACHE_PREFIX = 'appRoleCache:';

interface RoleCachePayload {
  roles: AppRole[];
}

function isValidRoleArray(value: unknown): value is AppRole[] {
  if (!Array.isArray(value)) {
    return false;
  }
  return value.every((item) => item === 'ADMIN' || item === 'TUTOR' || item === 'STUDENT');
}

function getRoleCacheKey(): string | null {
  const user = getAuthUser();
  if (!user?.userId) {
    return null;
  }
  return `${ROLE_CACHE_PREFIX}${user.userId}`;
}

function readRoleCache(): AppRole[] | null {
  const cacheKey = getRoleCacheKey();
  if (!cacheKey) {
    return null;
  }
  const raw = sessionStorage.getItem(cacheKey);
  if (!raw) {
    return null;
  }
  try {
    const parsed: unknown = JSON.parse(raw);
    if (
      parsed &&
      typeof parsed === 'object' &&
      isValidRoleArray((parsed as RoleCachePayload).roles)
    ) {
      return (parsed as RoleCachePayload).roles;
    }
    return null;
  } catch {
    return null;
  }
}

function writeRoleCache(roles: AppRole[]): void {
  const cacheKey = getRoleCacheKey();
  if (!cacheKey) {
    return;
  }
  const payload: RoleCachePayload = { roles };
  sessionStorage.setItem(cacheKey, JSON.stringify(payload));
}

export function clearRoleCache(): void {
  const cacheKey = getRoleCacheKey();
  if (cacheKey) {
    sessionStorage.removeItem(cacheKey);
    return;
  }
  const keysToDelete: string[] = [];
  for (let i = 0; i < sessionStorage.length; i += 1) {
    const key = sessionStorage.key(i);
    if (key && key.startsWith(ROLE_CACHE_PREFIX)) {
      keysToDelete.push(key);
    }
  }
  keysToDelete.forEach((key) => sessionStorage.removeItem(key));
}

export async function resolveRolesByApi(): Promise<AppRole[]> {
  const cachedRoles = readRoleCache();
  if (cachedRoles?.length) {
    return cachedRoles;
  }
  const user = getAuthUser();
  const roles: AppRole[] = user?.roles?.length ? user.roles : ['STUDENT'];
  writeRoleCache(roles);
  return roles;
}
