import { colors } from '../../styles/colors';

const styles = {
  primary: {
    background: colors.primary.main,
    color: '#fff',
    border: `1px solid ${colors.primary.dark}`,
  },
  secondary: {
    background: colors.neutral.white,
    color: colors.text.primary,
    border: `1px solid ${colors.neutral.borderStrong}`,
  },
  outline: {
    background: 'transparent',
    color: colors.text.primary,
    border: `1px solid ${colors.neutral.borderStrong}`,
  },
};

function Button({ children, type = 'button', variant = 'primary', disabled, loading, onClick }) {
  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      style={{
        ...styles[variant],
        borderRadius: '10px',
        padding: '10px 14px',
        fontWeight: 600,
        cursor: disabled || loading ? 'not-allowed' : 'pointer',
        width: '100%',
        opacity: disabled || loading ? 0.6 : 1,
      }}
    >
      {loading ? 'Loading...' : children}
    </button>
  );
}

export default Button;
