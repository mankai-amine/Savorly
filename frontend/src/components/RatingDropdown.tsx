import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';

interface RatingDropdownProps {
  recipeId: string;
}

interface RatingPayload {
  rating: number;
}

const RatingDropdown: React.FC<RatingDropdownProps> = ({ recipeId }) => {
  const [selectedRating, setSelectedRating] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const accessToken = sessionStorage.getItem("accessToken");
    if (!accessToken) {
      alert("Please log in to rate this recipe");
      return;
    }

    try {
      const payload: RatingPayload = { rating: parseInt(selectedRating) };
      await fetch(`http://localhost:8080/api/rating/create/${recipeId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`
        },
        body: JSON.stringify(payload)
      });
      window.location.reload();
    } catch (err) {
      alert('Error submitting rating');
    }
  };

  return (
    <Form onSubmit={handleSubmit} className="d-flex gap-2 align-items-center mt-2">
      <Form.Select 
        value={selectedRating} 
        onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setSelectedRating(e.target.value)}
        style={{ width: 'auto' }}
      >
        <option value="">Rate this recipe</option>
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
      </Form.Select>
      <Button type="submit" disabled={!selectedRating}>Submit</Button>
    </Form>
  );
};

export default RatingDropdown;