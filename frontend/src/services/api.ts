import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import {
  clearAuthSession,
  getAccessToken,
  getRefreshToken,
  saveAuthSession,
} from '../utils/storage';
import { AppRole } from '../types/app';

interface RetryRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

interface RefreshTokenResponse {
  userId: string;
  email: string;
  name: string;
  accessToken: string;
  refreshToken: string;
  needsProfileCompletion: boolean;
  needsTutorOnboarding: boolean;
  roles: AppRole[];
  activeRole: AppRole;
}

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api',
});

const PUBLIC_AUTH_ENDPOINTS = new Set([
  '/auth/register',
  '/auth/verify-otp',
  '/auth/resend-otp',
  '/auth/login',
  '/auth/refresh',
  '/auth/google',
]);

function normalizePath(url?: string): string {
  if (!url) {
    return '';
  }
  if (url.startsWith('http://') || url.startsWith('https://')) {
    try {
      const parsed = new URL(url);
      return parsed.pathname;
    } catch {
      return url;
    }
  }
  return url.startsWith('/') ? url : `/${url}`;
}

function isPublicAuthEndpoint(url?: string): boolean {
  const path = normalizePath(url);
  if (PUBLIC_AUTH_ENDPOINTS.has(path)) {
    return true;
  }
  // Some runtime/build combinations keep "/api" prefix in axios config.url.
  if (path.startsWith('/api/')) {
    const withoutApiPrefix = path.substring(4);
    return PUBLIC_AUTH_ENDPOINTS.has(withoutApiPrefix);
  }
  return false;
}

api.interceptors.request.use((config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
  const token = getAccessToken();
  if (token && !isPublicAuthEndpoint(config.url)) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response: AxiosResponse): AxiosResponse => response,
  async (error: AxiosError): Promise<AxiosResponse> => {
    const originalRequest = (error.config || {}) as RetryRequestConfig;
    const requestIsPublicAuth = isPublicAuthEndpoint(originalRequest.url);
    if (error.response?.status === 401 && !originalRequest._retry && !requestIsPublicAuth) {
      originalRequest._retry = true;
      try {
        const refreshToken = getRefreshToken();
        if (!refreshToken) {
          clearAuthSession();
          return Promise.reject(error);
        }
        const response = await axios.post<RefreshTokenResponse>(`${api.defaults.baseURL}/auth/refresh`, { refreshToken });
        const refreshPayload = response.data;
        saveAuthSession({
          userId: refreshPayload.userId,
          email: refreshPayload.email,
          name: refreshPayload.name,
          accessToken: refreshPayload.accessToken,
          refreshToken: refreshPayload.refreshToken,
          needsProfileCompletion: !!refreshPayload.needsProfileCompletion,
          needsTutorOnboarding: !!refreshPayload.needsTutorOnboarding,
          roles: refreshPayload.roles,
          activeRole: refreshPayload.activeRole,
        });
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${refreshPayload.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        clearAuthSession();
        window.location.href = '/';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
