import { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import axios from 'axios';

const apiUrl = 'http://localhost:8080/api/favourites'; 

interface Recipe {
    id: number;
    name: string;
    ingredients: string;
    instructions: string;
    picture: string;
    authorId: number;
}

export const Favourites = () => {
    const [favouriteRecipes, setFavouriteRecipes] = useState<Recipe[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const accessToken = sessionStorage.getItem("accessToken"); 
    if (!accessToken) {
        alert("Authorization token is missing. Please log in again.");
        return;
    }

    useEffect(() => {

    const fetchRecipes = async () => {
        try {
        const response = await axios.get(`${apiUrl}`, {
            headers: { Authorization: `Bearer ${accessToken}` },
            });
            setFavouriteRecipes(response.data as Recipe[]);
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
            await axios.delete(`${apiUrl}/${recipeId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });

            const response = await axios.get(`${apiUrl}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });

            setFavouriteRecipes(response.data as Recipe[]);
              
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
        <h1 className="mb-4">Favourite Recipes</h1>
        {Array.isArray(favouriteRecipes) && favouriteRecipes.length === 0 ? (
        <div className="alert alert-info" role="alert">
          No recipes found.
        </div>
        ) : (
        <Row>
          {favouriteRecipes.map((recipe) => (
            <Col key={recipe.id} md={4} className="mb-4">
              <Card>
                <div style={{ position: 'relative' }}>
                    <Button
                    variant="link"
                    onClick={() => handleDelete(recipe.id)}
                    style={{
                        position: 'absolute',
                        top: '10px',
                        right: '10px',
                        color: 'red',
                        fontSize: '1.5rem',
                        padding: '0',
                        textDecoration: 'none'
                    }}
                    >
                    &times; {/* Close icon (X) */}
                    </Button>
                </div>
                <Card.Body>
                    <Card.Title>{recipe.name}</Card.Title>
                    <Card.Text>
                    <strong>Ingredients:</strong> {recipe.ingredients}
                    </Card.Text>
                    <Card.Text>
                    <strong>Instructions:</strong> {recipe.instructions}
                    </Card.Text>
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