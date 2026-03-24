import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import api from '../services/api';
import { clearAuthSession, getAuthUser } from '../utils/storage';
import { logout } from '../services/authService';

function DashboardPage() {
  const [profile, setProfile] = useState(null);
  const user = getAuthUser();
  const navigate = useNavigate();

  useEffect(() => {
    async function loadProfile() {
      try {
        const response = await api.get('/users/me/profile');
        setProfile(response.data);
      } catch (error) {
        const status = error?.response?.status;
        if (status === 401 || status === 403) {
          clearAuthSession();
          navigate('/');
        }
      }
    }
    loadProfile();
  }, [navigate]);

  async function handleLogout() {
    try {
      await logout();
    } finally {
      clearAuthSession();
      navigate('/');
    }
  }

  return (
    <div className="page">
      <div className="container">
        <h1 className="title">Dashboard</h1>
        <p className="subtitle">Welcome back, {profile?.name || user?.name || user?.email}.</p>

        <div className="grid-3" style={{ marginTop: 18 }}>
          <Card featured>
            <h3 className="title" style={{ fontSize: 18 }}>
              User Profile
            </h3>
            <p>Email: {profile?.email || user?.email}</p>
            <p>Phone: {profile?.phoneNumber || 'Not provided'}</p>
          </Card>
          <Card>
            <h3 className="title" style={{ fontSize: 18 }}>
              Status
            </h3>
            <div className="badge-row">
              <Badge>Authenticated</Badge>
              <Badge>Profile Ready</Badge>
            </div>
          </Card>
          <Card>
            <h3 className="title" style={{ fontSize: 18 }}>
              Session
            </h3>
            <Button onClick={handleLogout}>Logout</Button>
          </Card>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
