import api from './api';
import { saveAuthSession, setNeedsProfileCompletion } from '../utils/storage';

function inferNeedsProfileCompletion(profile) {
  const hasPhone = !!profile?.phoneNumber?.trim?.();
  const hasFacebook = !!profile?.facebookUrl?.trim?.();
  return !(hasPhone || hasFacebook);
}

export async function login(payload) {
  const response = await api.post('/auth/login', payload);
  const data = response.data;
  saveAuthSession({
    userId: data.userId,
    email: data.email,
    name: data.email?.split('@')[0] || 'User',
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
    needsProfileCompletion: true,
  });

  try {
    const profileResponse = await api.get('/users/me/profile');
    setNeedsProfileCompletion(inferNeedsProfileCompletion(profileResponse.data));
  } catch {
    // Keep conservative default (true) if profile fetch fails.
  }
  return data;
}

export async function register(payload) {
  return api.post('/auth/register', payload);
}

export async function verifyOtp(payload) {
  const response = await api.post('/auth/verify-otp', payload);
  const data = response.data;
  saveAuthSession({
    userId: data.userId,
    email: data.email,
    name: data.email?.split('@')[0] || 'User',
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
    needsProfileCompletion: true,
  });
  return data;
}

export async function logout() {
  await api.post('/auth/logout');
}
