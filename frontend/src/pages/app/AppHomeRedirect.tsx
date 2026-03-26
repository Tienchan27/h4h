import { Navigate } from 'react-router-dom';
import { useAppAccess } from '../../context/AppAccessContext';
import { getAuthUser } from '../../utils/storage';

function AppHomeRedirect() {
  const { roles } = useAppAccess();
  const activeRole = getAuthUser()?.activeRole;
  if (activeRole === 'ADMIN') {
    return <Navigate to="/app/admin/tutors" replace />;
  }
  if (activeRole === 'TUTOR') {
    return <Navigate to="/app/tutor/dashboard" replace />;
  }
  if (activeRole === 'STUDENT') {
    return <Navigate to="/app/student/classes" replace />;
  }
  if (roles.includes('ADMIN')) {
    return <Navigate to="/app/admin/tutors" replace />;
  }
  if (roles.includes('TUTOR')) {
    return <Navigate to="/app/tutor/dashboard" replace />;
  }
  return <Navigate to="/app/student/classes" replace />;
}

export default AppHomeRedirect;
