import { colors } from '../../styles/colors';

function Badge({ children }) {
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '6px 12px',
        borderRadius: 999,
        background: colors.primary.light,
        color: colors.primary.dark,
        border: `1px solid ${colors.primary.main}`,
        fontWeight: 600,
      }}
    >
      {children}
    </span>
  );
}

export default Badge;
