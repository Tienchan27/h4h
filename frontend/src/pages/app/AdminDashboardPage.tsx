import { FormEvent, useEffect, useState } from 'react';
import { getAdminTutorDetail, getAdminTutorSummary, inviteTutor } from '../../services/dashboardService';
import { TutorDashboardResponse, TutorSummaryResponse } from '../../types/dashboard';
import { extractApiErrorMessage } from '../../services/authService';

function getCurrentMonth(): string {
  const now = new Date();
  return `${now.getFullYear()}-${`${now.getMonth() + 1}`.padStart(2, '0')}`;
}

function AdminDashboardPage() {
  const [month, setMonth] = useState<string>(getCurrentMonth());
  const [items, setItems] = useState<TutorSummaryResponse[]>([]);
  const [detail, setDetail] = useState<TutorDashboardResponse | null>(null);
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
            <h2 className="title title-lg">Admin Dashboard</h2>
            <p className="subtitle">Monthly tutor payout overview and performance summary.</p>
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
                    <td>{item.tutorEmail}</td>
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
          <div className="grid-3">
            <div className="panel">
              <strong>Period</strong>
              <p>{detail.year}-{`${detail.month}`.padStart(2, '0')}</p>
            </div>
            <div className="panel">
              <strong>Gross revenue</strong>
              <p>{detail.grossRevenue.toLocaleString()}</p>
            </div>
            <div className="panel">
              <strong>Net salary</strong>
              <p>{detail.netSalary.toLocaleString()}</p>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default AdminDashboardPage;
