import api from './api';
import {
  ApplyClassResponse,
  AvailableClassResponse,
  PublishClassRequest,
  StudentLookupResponse,
  PublishedClassResponse,
  SubjectOptionResponse,
  TutorClassApplicationResponse,
} from '../types/classAssignment';

export async function listSubjects(): Promise<SubjectOptionResponse[]> {
  const response = await api.get<SubjectOptionResponse[]>('/admin/classes/subjects');
  return response.data;
}

export async function publishClass(payload: PublishClassRequest): Promise<PublishedClassResponse> {
  const response = await api.post<PublishedClassResponse>('/admin/classes/publish', payload);
  return response.data;
}

export async function lookupStudentByEmail(email: string): Promise<StudentLookupResponse> {
  const response = await api.get<StudentLookupResponse>('/admin/classes/students/lookup', {
    params: { email },
  });
  return response.data;
}

export async function listPublishedClasses(): Promise<PublishedClassResponse[]> {
  const response = await api.get<PublishedClassResponse[]>('/admin/classes/published');
  return response.data;
}

export async function listClassApplications(classId: string): Promise<TutorClassApplicationResponse[]> {
  const response = await api.get<TutorClassApplicationResponse[]>(`/admin/classes/${classId}/applications`);
  return response.data;
}

export async function approveClassApplication(applicationId: string): Promise<PublishedClassResponse> {
  const response = await api.post<PublishedClassResponse>(`/admin/classes/applications/${applicationId}/approve`);
  return response.data;
}

export async function rejectClassApplication(applicationId: string, reason?: string): Promise<TutorClassApplicationResponse> {
  const response = await api.post<TutorClassApplicationResponse>(`/admin/classes/applications/${applicationId}/reject`, {
    reason: reason || null,
  });
  return response.data;
}

export async function updateClassDisplayName(classId: string, displayName: string): Promise<PublishedClassResponse> {
  const response = await api.patch<PublishedClassResponse>(`/classes/${classId}/display-name`, { displayName });
  return response.data;
}

export async function listAvailableClasses(): Promise<AvailableClassResponse[]> {
  const response = await api.get<AvailableClassResponse[]>('/classes/available');
  return response.data;
}

export async function applyClass(classId: string): Promise<ApplyClassResponse> {
  const response = await api.post<ApplyClassResponse>(`/classes/${classId}/apply`);
  return response.data;
}
