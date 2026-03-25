import { getAuthUser, saveAuthSession, setNeedsTutorOnboarding } from './storage';

describe('storage tutor onboarding flag', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('persists tutor onboarding state in auth user', () => {
    saveAuthSession({
      userId: 'u-1',
      email: 'tutor@example.com',
      name: 'Tutor',
      accessToken: 'access',
      refreshToken: 'refresh',
      needsProfileCompletion: false,
      needsTutorOnboarding: true,
    });

    expect(getAuthUser()?.needsTutorOnboarding).toBe(true);

    setNeedsTutorOnboarding(false);
    expect(getAuthUser()?.needsTutorOnboarding).toBe(false);
  });

  test('clears stale auth schema and tokens', () => {
    localStorage.setItem('accessToken', 'stale-access');
    localStorage.setItem('refreshToken', 'stale-refresh');
    localStorage.setItem(
      'authUser',
      JSON.stringify({
        userId: 'u-legacy',
        email: 'legacy@example.com',
        name: 'Legacy',
        picture: null,
        needsProfileCompletion: false,
      })
    );

    expect(getAuthUser()).toBeNull();
    expect(localStorage.getItem('accessToken')).toBeNull();
    expect(localStorage.getItem('refreshToken')).toBeNull();
    expect(localStorage.getItem('authUser')).toBeNull();
  });
});
