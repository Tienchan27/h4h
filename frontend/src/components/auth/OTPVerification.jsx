import { useState } from 'react';
import Button from '../ui/Button';
import Input from '../ui/Input';
import { verifyOtp } from '../../services/authService';

function OTPVerification({ email, onSuccess, onError }) {
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    try {
      await verifyOtp({ email, otp });
      onSuccess?.();
    } catch (error) {
      onError?.(error?.response?.data?.message || 'OTP verification failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <p style={{ marginTop: 0, color: '#64748b' }}>Enter the OTP sent to: {email}</p>
      <Input label="One-Time Password" value={otp} onChange={(e) => setOtp(e.target.value)} />
      <Button type="submit" loading={loading}>
        Verify OTP
      </Button>
    </form>
  );
}

export default OTPVerification;
