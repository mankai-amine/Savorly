import React, { useState } from 'react';
import { Star } from 'lucide-react';

interface StarRatingProps {
  recipeId: string;
  initialRating?: number;
  onRatingSubmit: (rating: number) => Promise<void>;
}

interface RatingResponse {
  message?: string;
}

const StarRating: React.FC<StarRatingProps> = ({ 
  recipeId, 
  initialRating = 0, 
  onRatingSubmit 
}) => {
  const [rating, setRating] = useState<number>(initialRating);
  const [hover, setHover] = useState<number>(0);
  const [error, setError] = useState<string>('');
  const [submitted, setSubmitted] = useState<boolean>(false);

  const handleRatingSubmit = async (): Promise<void> => {
    try {
      const accessToken = sessionStorage.getItem("accessToken");
      if (!accessToken) {
        setError("Please log in to rate this recipe");
        return;
      }

      const response = await fetch(`http://localhost:8080/api/rating/create/${recipeId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify({ rating })
      });

      if (response.ok) {
        setSubmitted(true);
        // Trigger parent component to fetch new average
        await onRatingSubmit(0); // we pass 0 as we don't want to set this as the new average
      } else {
        const data: RatingResponse = await response.json();
        setError(data.message || 'Failed to submit rating');
      }
    } catch (err) {
      setError('Error submitting rating');
    }
  };

  return (
    <div className="flex flex-col items-start gap-2">
      <div className="flex items-center gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            size={24}
            className="cursor-pointer transition-colors"
            color={star <= (hover || rating) ? '#FACC15' : '#D1D5DB'}
            fill={star <= (hover || rating) ? '#FACC15' : 'none'}
            onMouseEnter={() => setHover(star)}
            onMouseLeave={() => setHover(0)}
            onClick={() => {
              setRating(star);
              setSubmitted(false);
              setError('');
            }}
          />
        ))}
      </div>
      {rating > 0 && !submitted && (
        <button
          onClick={handleRatingSubmit}
          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
          type="button"
        >
          Submit Rating
        </button>
      )}
      {error && <p className="text-red-500 text-sm">{error}</p>}
      {submitted && <p className="text-green-500 text-sm">Rating submitted!</p>}
    </div>
  );
};

export default StarRating;