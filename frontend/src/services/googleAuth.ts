import { AxiosError, AxiosResponse } from 'axios';
import api from './api';
import { clearAuthSession, saveAuthSession } from '../utils/storage';
import { AuthSessionPayload } from '../types/auth';

export async function googleLogin(idToken: string): Promise<AuthSessionPayload> {
  try {
    const response = await api.post<AuthSessionPayload>('/auth/google', { idToken });
    saveAuthSession(response.data);
    return response.data;
  } catch (error: unknown) {
    const status = (error as AxiosError)?.response?.status;
    if (status !== 401) {
      throw error;
    }

    // Recover from stale client session immediately after rebuild/deploy.
    clearAuthSession();
    const retryResponse = await api.post<AuthSessionPayload>('/auth/google', { idToken });
    saveAuthSession(retryResponse.data);
    return retryResponse.data;
  }
}

export async function linkGoogleAccount(idToken: string, currentPassword: string): Promise<AxiosResponse<unknown>> {
  return api.post('/auth/google/link', { idToken, currentPassword });
}
