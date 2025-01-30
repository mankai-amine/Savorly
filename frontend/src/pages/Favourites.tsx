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
        <h1 className="text-center mb-5" 
            style={{ fontSize: '2rem', fontWeight: 'bold', color: 'rgb(213, 66, 21)', letterSpacing: '2px', textShadow: '2px 2px 4px rgba(0, 0, 0, 0.2)' }}>
          FAVOURITE RECIPES
        </h1>
          {Array.isArray(favouriteRecipes) && favouriteRecipes.length === 0 ? (
            <div className="text-center" style={{ fontSize: '1.2rem' }}>
              No recipes found.
            </div>
          ) : (
            <Row>
              {favouriteRecipes.map((recipe) => (
                <Col key={recipe.id} md={4} className="mb-4">
                  <Card className="shadow-sm rounded-lg" style={{ backgroundColor: '#fff' }}>
                    
                    <Card.Img
                      variant="top"
                      src={recipe.picture} 
                      alt={recipe.name}
                      style={{ height: '200px', objectFit: 'fill', borderTopLeftRadius: '10px', borderTopRightRadius: '10px' }}
                    />
                    <Card.Body>
                      <div className="d-flex justify-content-between align-items-center">
                        <Card.Title className="text-uppercase" style={{ fontWeight: '500', color: '#2c3e50' }}>{recipe.name}</Card.Title>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(recipe.id)}
                          style={{
                            border: 'none',
                            backgroundColor: 'transparent',
                            color: '#e74c3c',
                            fontSize: '1.5rem',
                            fontWeight: 'bold',
                            //padding: '0rem 0.5rem',
                            cursor: 'pointer',
                            borderRadius: '50%',
                          }}
                        >
                          <span>&times;</span>
                        </Button>

                      </div>
                      <Card.Text className="text-muted" style={{ fontSize: '1rem' }}>
                        <strong>Ingredients:</strong> {recipe.ingredients}
                      </Card.Text>
                      <Card.Text className="text-muted" style={{ fontSize: '1rem' }}>
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