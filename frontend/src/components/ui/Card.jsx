import { colors } from '../../styles/colors';

function Card({ children, featured = false }) {
  return (
    <div
      style={{
        background: colors.neutral.white,
        border: featured
          ? `1px solid ${colors.primary.main}`
          : `1px solid ${colors.neutral.border}`,
        borderRadius: '12px',
        padding: 16,
      }}
    >
      {children}
    </div>
  );
}

export default Card;
