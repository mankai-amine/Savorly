import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Card, Button } from 'react-bootstrap';
import axios from 'axios';

const apiUrl = 'http://localhost:8080/api/recipe'; 

export const RecipeDetails = () => {
  const { id } = useParams();
  const [recipe, setRecipe] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRecipe = async () => {
      try {
        const response = await axios.get(`${apiUrl}/${id}`);
        setRecipe(response.data);
      } catch (err) {
        setError('Failed to load recipe details');
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchRecipe();
  }, [id]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <Container className="mt-5">
      <h1 className="mb-4">{recipe.name}</h1>
      <Card>
        <Card.Body>
          <Card.Title>Ingredients</Card.Title>
          <Card.Text>{recipe.ingredients}</Card.Text>
          <Card.Title>Instructions</Card.Title>
          <Card.Text>{recipe.instructions}</Card.Text>
          <Button variant="secondary" href="/">Back to Home</Button>
        </Card.Body>
      </Card>
    </Container>
  );
};
