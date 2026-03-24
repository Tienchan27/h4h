import { useState } from 'react';
import Button from '../ui/Button';
import Card from '../ui/Card';
import Input from '../ui/Input';
import api from '../../services/api';
import { markProfileCompleted } from '../../utils/storage';
import { normalizeProfilePayload, requirePhoneOrFacebook } from '../../utils/validation';

function extractApiErrorMessage(error) {
  const details = error?.response?.data?.details;
  if (details && typeof details === 'object') {
    const firstDetail = Object.values(details).find((value) => typeof value === 'string' && value.trim());
    if (firstDetail) {
      return firstDetail;
    }
  }
  return error?.response?.data?.message || 'Failed to update profile';
}

function ProfileCompletion({ user, onCompleted, onError }) {
  const [form, setForm] = useState({
    phoneNumber: '',
    facebookUrl: '',
    parentPhone: '',
    address: '',
  });
  const [loading, setLoading] = useState(false);

  function updateField(name, value) {
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (!requirePhoneOrFacebook(form.phoneNumber, form.facebookUrl)) {
      onError?.('Provide at least phone number or Facebook URL');
      return;
    }

    setLoading(true);
    try {
      await api.patch('/users/me/profile', normalizeProfilePayload(form));
      markProfileCompleted();
      onCompleted?.();
    } catch (error) {
      onError?.(extractApiErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }

  return (
    <Card featured>
      <h2 className="title" style={{ fontSize: 24 }}>
        Complete Profile
      </h2>
      <p className="subtitle">Welcome {user?.name || 'user'}, complete your profile to continue.</p>
      <form onSubmit={handleSubmit}>
        <Input label="Email (from Google)" value={user?.email || ''} disabled />
        <Input
          label="Phone Number"
          value={form.phoneNumber}
          onChange={(e) => updateField('phoneNumber', e.target.value)}
        />
        <Input
          label="Facebook URL"
          value={form.facebookUrl}
          onChange={(e) => updateField('facebookUrl', e.target.value)}
        />
        <Input
          label="Parent Phone (optional)"
          value={form.parentPhone}
          onChange={(e) => updateField('parentPhone', e.target.value)}
        />
        <Input label="Address (optional)" value={form.address} onChange={(e) => updateField('address', e.target.value)} />
        <Button type="submit" loading={loading}>
          Save Profile
        </Button>
      </form>
    </Card>
  );
}

export default ProfileCompletion;
