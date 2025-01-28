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
        const isConfirmed = window.confirm('Are you sure you want to remove this recipe from your favourites?');
        if (!isConfirmed) return;
        try {
            await axios.delete(`${apiUrl}/${recipeId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });

            const response = await axios.get(`${apiUrl}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });

            setFavouriteRecipes(response.data as Recipe[]);
              
            alert('Recipe successfully removed from favourites.');
            } catch (err) {
            setError('Failed to load recipes');
            } finally {
            setLoading(false);
        }
    }

    return (
      <div style={{ minHeight: '100vh' }}>
        <Container className="mt-5">
          <h1 className="text-center mb-4" style={{ fontSize: '2.5rem', fontWeight: '600', color: '#333' }}>Favourite Recipes</h1>
          {Array.isArray(favouriteRecipes) && favouriteRecipes.length === 0 ? (
            <div className="alert alert-info text-center" role="alert" style={{ fontSize: '1.2rem' }}>
              No recipes found.
            </div>
          ) : (
            <Row>
              {favouriteRecipes.map((recipe) => (
                <Col key={recipe.id} md={4} className="mb-4">
                  <Card className="shadow-sm rounded-lg" style={{ backgroundColor: '#fff' }}>
                    <div style={{ position: 'relative' }}>
                      <Button
                        variant="link"
                        onClick={() => handleDelete(recipe.id)}
                        style={{
                          position: 'absolute',
                          top: '10px',
                          right: '10px',
                          color: '#e74c3c',
                          fontSize: '1.5rem',
                          padding: '0',
                          textDecoration: 'none',
                          backgroundColor: 'transparent',
                          border: 'none'
                        }}
                      >
                        &times; {/* Close icon (X) */}
                      </Button>
                    </div>
                    <Card.Img
                      variant="top"
                      src={recipe.picture} 
                      alt={recipe.name}
                      style={{ height: '200px', objectFit: 'cover', borderTopLeftRadius: '10px', borderTopRightRadius: '10px' }}
                    />
                    <Card.Body>
                      <Card.Title className="text-uppercase" style={{ fontWeight: '500', color: '#2c3e50' }}>{recipe.name}</Card.Title>
                      <Card.Text className="text-muted" style={{ fontSize: '0.95rem' }}>
                        <strong>Ingredients:</strong> {recipe.ingredients}
                      </Card.Text>
                      <Card.Text className="text-muted" style={{ fontSize: '0.95rem' }}>
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