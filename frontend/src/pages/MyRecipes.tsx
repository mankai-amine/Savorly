import { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const apiUrl = 'http://localhost:8080/api/recipes'; 

interface Recipe {
    id: number;
    name: string;
    ingredients: string;
    instructions: string;
    picture: string;
    authorId: number;
}

export const MyRecipes = () => {
    const [myRecipes, setMyRecipes] = useState<Recipe[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const accessToken = sessionStorage.getItem("accessToken"); 
    if (!accessToken) {
        alert("Authorization token is missing. Please log in again.");
        return;
    }

    useEffect(() => {

      const fetchRecipes = async () => {
        try {
          const response = await axios.get<Recipe[]>(`${apiUrl}/mine`, {
            headers: { Authorization: `Bearer ${accessToken}` },
            });
          setMyRecipes(response.data as Recipe[]);
        } catch (err) {
        setError('Failed to load recipes');
        } finally {
        setLoading(false);
        }
    };

    fetchRecipes();
    }, []);

    if (loading) {
    return <div>Loading...</div>;
    }

    if (error) {
    return <div>{error}</div>;
    }

    async function handleDelete(recipeId: number): Promise<void> {
        const isConfirmed = window.confirm('Are you sure you want to delete this recipe?');
        if (!isConfirmed) return;
        try {
            await axios.delete(`${apiUrl}/delete/${recipeId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });

            const response = await axios.get(`${apiUrl}/mine`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });

            setMyRecipes(response.data as Recipe[]);
              
            alert('Recipe deleted successfully');
            } catch (err) {
            setError('Failed to load recipes');
            } finally {
            setLoading(false);
        }
    }

  return (
    <div style={{ backgroundColor: '#f0f0f0', minHeight: '100vh' }}>
      <Container className="mt-5">
        <h1 className="mb-4">My Recipes</h1>
        {Array.isArray(myRecipes) && myRecipes.length === 0 ? (
        <div className="alert alert-info" role="alert">
          No recipes found.
        </div>
        ) : (
        <Row>
          {myRecipes.map((recipe) => (
            <Col key={recipe.id} md={4} className="mb-4">
              <Card>
                <Card.Body>
                  <Card.Title>{recipe.name}</Card.Title>
                  <Card.Text>
                    <strong>Ingredients:</strong> {recipe.ingredients}
                  </Card.Text>
                  <Card.Text>
                    <strong>Instructions:</strong> {recipe.instructions}
                  </Card.Text>
                  <Button
                    variant="primary"
                    onClick={() => navigate(`/recipe/edit/${recipe.id}`)}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="danger"
                    onClick={() => handleDelete(recipe.id)}
                    style={{ marginLeft: '10px' }}
                  >
                    Delete
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
        )}
      </Container>
    </div>
  );
};
