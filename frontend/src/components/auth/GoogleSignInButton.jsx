import { GoogleLogin } from '@react-oauth/google';

function GoogleSignInButton({ onSuccess, onError }) {
  return (
    <div style={{ display: 'grid', placeItems: 'center', gap: 8 }}>
      <div style={{ fontWeight: 600 }}>Sign in with Google</div>
      <GoogleLogin
        onSuccess={(credentialResponse) => onSuccess?.(credentialResponse.credential)}
        onError={() => onError?.(new Error('Google sign in failed'))}
      />
    </div>
  );
}

export default GoogleSignInButton;
