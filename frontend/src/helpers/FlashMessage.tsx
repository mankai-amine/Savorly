interface FlashMessageProps {
  message: string;
  type: string;
  onClose: () => void;
}

const FlashMessage = ({ message, type, onClose }: FlashMessageProps) => {
  return (
    <div className={`alert alert-${type} alert-dismissible fade show`} role="alert">
      {message}
      <button type="button" className="btn-close" onClick={onClose} aria-label="Close"></button>
    </div>
  );
};

export default FlashMessage;
