import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import LoginForm from '../components/auth/LoginForm';
import RegisterForm from '../components/auth/RegisterForm';
import OTPVerification from '../components/auth/OTPVerification';
import GoogleSignInButton from '../components/auth/GoogleSignInButton';
import { getAuthUser, isAuthenticated } from '../utils/storage';
import { googleLogin } from '../services/googleAuth';
import { colors } from '../styles/colors';

function LandingPage() {
  const [tab, setTab] = useState('login');
  const [error, setError] = useState('');
  const [otpEmail, setOtpEmail] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated()) {
      routeByProfileFlag();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function routeByProfileFlag() {
    const user = getAuthUser();
    if (user?.needsProfileCompletion !== false) {
      navigate('/profile-completion');
      return;
    }
    navigate('/dashboard');
  }

  async function handleGoogleLogin(idToken) {
    try {
      setError('');
      await googleLogin(idToken);
      routeByProfileFlag();
    } catch (err) {
      const message = err?.response?.data?.message || 'Google login failed';
      if (message === 'EMAIL_CONFLICT') {
        setError('This email is already linked to a password account. Sign in with password first, then link Google in account settings.');
        return;
      }
      setError(message);
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1 className="title">Tutor Management System</h1>
        <p className="subtitle">Authentication demo and API testing interface</p>

        <div className="grid-2" style={{ alignItems: 'start', marginTop: 18 }}>
          <Card featured>
            <div style={{ display: 'flex', gap: 8, marginBottom: 14 }}>
              <button
                onClick={() => setTab('login')}
                style={{
                  padding: '8px 12px',
                  borderRadius: 8,
                  border: `1px solid ${tab === 'login' ? colors.primary.main : colors.neutral.borderStrong}`,
                  background: tab === 'login' ? colors.primary.light : colors.neutral.white,
                  cursor: 'pointer',
                }}
              >
                Sign In
              </button>
              <button
                onClick={() => setTab('register')}
                style={{
                  padding: '8px 12px',
                  borderRadius: 8,
                  border: `1px solid ${tab === 'register' ? colors.primary.main : colors.neutral.borderStrong}`,
                  background: tab === 'register' ? colors.primary.light : colors.neutral.white,
                  cursor: 'pointer',
                }}
              >
                Sign Up
              </button>
              <button
                onClick={() => navigate('/api-tester')}
                style={{
                  marginLeft: 'auto',
                  padding: '8px 12px',
                  borderRadius: 8,
                  border: `1px solid ${colors.neutral.borderStrong}`,
                  background: colors.neutral.white,
                  cursor: 'pointer',
                }}
              >
                API Tester
              </button>
            </div>
            {otpEmail ? (
              <OTPVerification email={otpEmail} onSuccess={routeByProfileFlag} onError={setError} />
            ) : tab === 'login' ? (
              <LoginForm onSuccess={routeByProfileFlag} onError={setError} />
            ) : (
              <RegisterForm onRegistered={setOtpEmail} onError={setError} />
            )}

            <div className="auth-separator">or</div>
            <GoogleSignInButton onSuccess={handleGoogleLogin} onError={(err) => setError(err.message)} />

            {error ? <p style={{ color: colors.error, fontWeight: 600 }}>{error}</p> : null}
          </Card>

          <div className="grid-3">
            <Card>
              <h3 className="title" style={{ fontSize: 18 }}>
                Authentication
              </h3>
              <div className="badge-row">
                <Badge>Email and password</Badge>
                <Badge>Google OAuth</Badge>
              </div>
            </Card>
            <Card>
              <h3 className="title" style={{ fontSize: 18 }}>
                Profile
              </h3>
              <p className="muted">Profile completion and secure session management.</p>
            </Card>
            <Card>
              <h3 className="title" style={{ fontSize: 18 }}>
                API Demo
              </h3>
              <p className="muted">Interactive endpoint testing with request and response preview.</p>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LandingPage;
