export interface TutorSummaryResponse {
  tutorId: string;
  tutorEmail: string;
  grossRevenue: number;
  netSalary: number;
  payoutStatus: string;
}

export interface TutorDashboardResponse {
  year: number;
  month: number;
  grossRevenue: number;
  netSalary: number;
  status: string;
}

export interface TutorClassOverviewResponse {
  classId: string;
  subjectName: string;
  classStatus: string;
  pricePerHour: number;
  defaultSalaryRate: number;
  sessionCount: number;
  latestSessionDate: string | null;
}

export interface InviteTutorRequest {
  email: string;
}

export interface InviteTutorResponse {
  email: string;
  status: string;
  existingUser: boolean;
  tutorRoleAssigned: boolean;
  message: string;
}
