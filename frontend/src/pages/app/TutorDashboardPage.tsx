import { useEffect, useState } from 'react';
import { getTutorClassOverview, getTutorDashboard } from '../../services/dashboardService';
import { TutorClassOverviewResponse, TutorDashboardResponse } from '../../types/dashboard';
import { extractApiErrorMessage } from '../../services/authService';

function TutorDashboardPage() {
  const [items, setItems] = useState<TutorDashboardResponse[]>([]);
  const [classes, setClasses] = useState<TutorClassOverviewResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    async function load(): Promise<void> {
      setLoading(true);
      setError('');
      try {
        const [dashboardResponse, classResponse] = await Promise.all([getTutorDashboard(), getTutorClassOverview()]);
        setItems(dashboardResponse);
        setClasses(classResponse);
      } catch (err: unknown) {
        setError(extractApiErrorMessage(err, 'Failed to load tutor dashboard'));
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  return (
    <div className="stack-16">
      <div className="card">
        <h2 className="title title-lg">Tutor Dashboard</h2>
        <p className="subtitle">Track your monthly teaching revenue and salary status.</p>
        {loading ? <p className="muted">Loading...</p> : null}
        {error ? <p className="error-text">{error}</p> : null}
        {!loading && !items.length ? <p className="muted">No payout records yet.</p> : null}
        {!!items.length ? (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Month</th>
                  <th>Gross Revenue</th>
                  <th>Net Salary</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={`${item.year}-${item.month}`}>
                    <td>{item.year}-{`${item.month}`.padStart(2, '0')}</td>
                    <td>{item.grossRevenue.toLocaleString()}</td>
                    <td>{item.netSalary.toLocaleString()}</td>
                    <td>{item.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </div>

      <div className="card">
        <h3 className="section-title">Your classes overview</h3>
        {!loading && !classes.length ? <p className="muted">No assigned classes yet.</p> : null}
        {!!classes.length ? (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Subject</th>
                  <th>Status</th>
                  <th>Price/Hour</th>
                  <th>Salary Rate</th>
                  <th>Sessions</th>
                  <th>Latest Session</th>
                </tr>
              </thead>
              <tbody>
                {classes.map((item) => (
                  <tr key={item.classId}>
                    <td>{item.subjectName}</td>
                    <td>{item.classStatus}</td>
                    <td>{item.pricePerHour.toLocaleString()}</td>
                    <td>{(item.defaultSalaryRate * 100).toFixed(2)}%</td>
                    <td>{item.sessionCount}</td>
                    <td>{item.latestSessionDate || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </div>
    </div>
  );
}

export default TutorDashboardPage;
