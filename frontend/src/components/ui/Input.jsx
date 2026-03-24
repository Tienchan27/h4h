import { colors } from '../../styles/colors';

function Input({ label, icon, ...props }) {
  return (
    <label style={{ display: 'block', marginBottom: 12 }}>
      <div style={{ fontSize: 12, fontWeight: 600, marginBottom: 6, color: colors.text.secondary }}>
        {label}
      </div>
      <div
        style={{
          border: `1px solid ${colors.neutral.borderStrong}`,
          borderRadius: 10,
          background: '#fff',
          display: 'flex',
          alignItems: 'center',
          padding: '0 12px',
        }}
      >
        {icon ? <span style={{ marginRight: 8 }}>{icon}</span> : null}
        <input
          {...props}
          style={{
            border: 'none',
            outline: 'none',
            width: '100%',
            padding: '10px 0',
            background: 'transparent',
            color: colors.text.primary,
          }}
        />
      </div>
    </label>
  );
}

export default Input;
