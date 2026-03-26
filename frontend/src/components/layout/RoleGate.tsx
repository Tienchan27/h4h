import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { AppRole } from '../../types/app';
import { useAppAccess } from '../../context/AppAccessContext';
import { getAuthUser } from '../../utils/storage';

interface RoleGateProps {
  allowed: AppRole[];
  children: ReactNode;
}

function RoleGate({ allowed, children }: RoleGateProps) {
  const { roles } = useAppAccess();
  const activeRole = getAuthUser()?.activeRole;
  const canAccess = allowed.some((role) => roles.includes(role));
  const activeRoleAllowed = activeRole ? allowed.includes(activeRole) : false;
  if (!canAccess || !activeRoleAllowed) {
    return <Navigate to="/app/unauthorized" replace />;
  }
  return <>{children}</>;
}

export default RoleGate;
