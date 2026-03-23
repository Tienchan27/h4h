import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import LoginForm from '../components/auth/LoginForm';
import RegisterForm from '../components/auth/RegisterForm';
import OTPVerification from '../components/auth/OTPVerification';
import GoogleSignInButton from '../components/auth/GoogleSignInButton';
import { getAuthUser } from '../utils/storage';
import { googleLogin } from '../services/googleAuth';

function LandingPage() {
  const [tab, setTab] = useState('login');
  const [error, setError] = useState('');
  const [otpEmail, setOtpEmail] = useState('');
  const navigate = useNavigate();

  function routeByProfileFlag() {
    const user = getAuthUser();
    if (user?.needsProfileCompletion) {
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
        setError('Email da ton tai voi mat khau. Vui long dang nhap bang mat khau truoc, sau do vao tai khoan de lien ket Google.');
        return;
      }
      setError(message);
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1 className="title">🌸 TMS - Tutor Management System ✨</h1>
        <p className="subtitle">📚 Study Smart · 🎯 Achieve More</p>

        <div className="grid-2" style={{ alignItems: 'start', marginTop: 18 }}>
          <Card featured>
            <div style={{ display: 'flex', gap: 8, marginBottom: 14 }}>
              <button onClick={() => setTab('login')}>Dang nhap</button>
              <button onClick={() => setTab('register')}>Dang ky</button>
            </div>
            {otpEmail ? (
              <OTPVerification email={otpEmail} onSuccess={routeByProfileFlag} onError={setError} />
            ) : tab === 'login' ? (
              <LoginForm onSuccess={routeByProfileFlag} onError={setError} />
            ) : (
              <RegisterForm onRegistered={setOtpEmail} onError={setError} />
            )}

            <div className="auth-separator">-------- HOAC --------</div>
            <GoogleSignInButton onSuccess={handleGoogleLogin} onError={(err) => setError(err.message)} />

            {error ? <p style={{ color: '#E76F51', fontWeight: 700 }}>{error}</p> : null}
          </Card>

          <div className="grid-3">
            <Card>
              <h3 className="title" style={{ fontSize: 18 }}>
                Thanh tich
              </h3>
              <div className="badge-row">
                <Badge>🌟 IELTS 8.0</Badge>
                <Badge>🏆 SAT 1500+</Badge>
              </div>
            </Card>
            <Card>
              <h3 className="title" style={{ fontSize: 18 }}>
                Feature
              </h3>
              <p>🎓 Quan ly gia su thong minh</p>
            </Card>
            <Card>
              <h3 className="title" style={{ fontSize: 18 }}>
                Feature
              </h3>
              <p>📊 Theo doi tien do hoc tap</p>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LandingPage;
