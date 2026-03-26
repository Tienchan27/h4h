import { FormEvent, useEffect, useState } from 'react';
import { getAdminTutorDetail, getAdminTutorSummary, inviteTutor } from '../../services/dashboardService';
import { AdminTutorDetailResponse, TutorSummaryResponse } from '../../types/dashboard';
import { extractApiErrorMessage } from '../../services/authService';

function getCurrentMonth(): string {
  const now = new Date();
  return `${now.getFullYear()}-${`${now.getMonth() + 1}`.padStart(2, '0')}`;
}

function AdminTutorManagementPage() {
  const [month, setMonth] = useState<string>(getCurrentMonth());
  const [items, setItems] = useState<TutorSummaryResponse[]>([]);
  const [detail, setDetail] = useState<AdminTutorDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [inviteLoading, setInviteLoading] = useState<boolean>(false);
  const [inviteEmail, setInviteEmail] = useState<string>('');
  const [inviteMessage, setInviteMessage] = useState<string>('');
  const [error, setError] = useState<string>('');

  async function loadSummary(): Promise<void> {
    setLoading(true);
    setError('');
    try {
      const response = await getAdminTutorSummary(month);
      setItems(response);
    } catch (err: unknown) {
      setError(extractApiErrorMessage(err, 'Failed to load admin summary'));
    } finally {
      setLoading(false);
    }
  }

  async function loadDetail(tutorId: string): Promise<void> {
    setError('');
    try {
      const response = await getAdminTutorDetail(tutorId, month);
      setDetail(response);
    } catch (err: unknown) {
      setError(extractApiErrorMessage(err, 'Failed to load tutor detail'));
    }
  }

  async function handleInviteTutor(event: FormEvent<HTMLFormElement>): Promise<void> {
    event.preventDefault();
    setError('');
    setInviteMessage('');
    setInviteLoading(true);
    try {
      const response = await inviteTutor({ email: inviteEmail });
      setInviteMessage(response.message);
      setInviteEmail('');
      await loadSummary();
    } catch (err: unknown) {
      setError(extractApiErrorMessage(err, 'Failed to invite tutor'));
    } finally {
      setInviteLoading(false);
    }
  }

  useEffect(() => {
    loadSummary();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [month]);

  return (
    <div className="stack-16">
      <div className="card">
        <div className="page-header">
          <div>
            <h2 className="title title-lg">Tutor Management</h2>
            <p className="subtitle">Invite tutors and review payout snapshots by month.</p>
          </div>
          <input type="month" value={month} onChange={(event) => setMonth(event.target.value)} className="input-month" />
        </div>
        <form onSubmit={handleInviteTutor} className="grid-form">
          <input
            type="email"
            className="text-input"
            placeholder="New tutor email"
            value={inviteEmail}
            onChange={(event) => setInviteEmail(event.target.value)}
            required
          />
          <button className="btn btn-primary compact-btn" type="submit" disabled={inviteLoading}>
            {inviteLoading ? 'Adding...' : 'Add Tutor'}
          </button>
        </form>
        {inviteMessage ? <p className="success-text">{inviteMessage}</p> : null}
      </div>

      <div className="card">
        <h3 className="section-title">Tutor summary</h3>
        {loading ? <p className="muted">Loading...</p> : null}
        {error ? <p className="error-text">{error}</p> : null}
        {!loading && !items.length ? <p className="muted">No summary available for this month.</p> : null}
        {!!items.length ? (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Tutor</th>
                  <th>Gross Revenue</th>
                  <th>Net Salary</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.tutorId}>
                    <td>{item.tutorName} ({item.tutorEmail})</td>
                    <td>{item.grossRevenue.toLocaleString()}</td>
                    <td>{item.netSalary.toLocaleString()}</td>
                    <td>{item.payoutStatus}</td>
                    <td>
                      <button className="btn btn-outline table-action" onClick={() => loadDetail(item.tutorId)} type="button">
                        View detail
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </div>

      {detail ? (
        <div className="card">
          <h3 className="section-title">Selected tutor detail</h3>
          <div className="grid-3" style={{ marginBottom: 12 }}>
            <div className="panel">
              <strong>Name</strong>
              <p>{detail.name}</p>
            </div>
            <div className="panel">
              <strong>Email</strong>
              <p>{detail.email}</p>
            </div>
            <div className="panel">
              <strong>Phone</strong>
              <p>{detail.phoneNumber || '-'}</p>
            </div>
          </div>
          <p className="muted">Facebook: {detail.facebookUrl || '-'}</p>
          <p className="muted">Address: {detail.address || '-'}</p>
          {detail.payout ? (
            <div className="panel" style={{ marginBottom: 12 }}>
              <strong>Selected month payout</strong>
              <p>
                {detail.payout.year}-{`${detail.payout.month}`.padStart(2, '0')} | Gross: {detail.payout.grossRevenue.toLocaleString()} |
                Net: {detail.payout.netSalary.toLocaleString()} | Status: {detail.payout.status}
              </p>
            </div>
          ) : (
            <p className="muted">No payout generated for selected month.</p>
          )}

          <h4 className="section-title">Bank accounts</h4>
          {!detail.bankAccounts.length ? <p className="muted">No bank accounts.</p> : null}
          {!!detail.bankAccounts.length ? (
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Bank</th>
                    <th>Account</th>
                    <th>Holder</th>
                    <th>Primary</th>
                    <th>Verified</th>
                  </tr>
                </thead>
                <tbody>
                  {detail.bankAccounts.map((account) => (
                    <tr key={account.id}>
                      <td>{account.bankName}</td>
                      <td>{account.maskedAccountNumber}</td>
                      <td>{account.accountHolderName}</td>
                      <td>{account.primary ? 'Yes' : 'No'}</td>
                      <td>{account.verified ? 'Yes' : 'No'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : null}

          <h4 className="section-title">Managed classes</h4>
          {!detail.managedClasses.length ? <p className="muted">No managed classes.</p> : null}
          {!!detail.managedClasses.length ? (
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Subject</th>
                    <th>Status</th>
                    <th>Price/Hour</th>
                    <th>Salary rate</th>
                    <th>Sessions</th>
                    <th>Latest Session</th>
                  </tr>
                </thead>
                <tbody>
                  {detail.managedClasses.map((managedClass) => (
                    <tr key={managedClass.classId}>
                      <td>{managedClass.subjectName}</td>
                      <td>{managedClass.classStatus}</td>
                      <td>{managedClass.pricePerHour.toLocaleString()}</td>
                      <td>{(managedClass.defaultSalaryRate * 100).toFixed(2)}%</td>
                      <td>{managedClass.sessionCount}</td>
                      <td>{managedClass.latestSessionDate || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}

export default AdminTutorManagementPage;
