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
      <div style={{minHeight: '100vh' }}>
        <Container className="mt-5">
        <h1 className="text-center mb-5" 
            style={{ fontSize: '2rem', fontWeight: 'bold', color: 'rgb(213, 66, 21)', letterSpacing: '2px', textShadow: '2px 2px 4px rgba(0, 0, 0, 0.2)' }}>
          MY RECIPES
        </h1>

          {Array.isArray(myRecipes) && myRecipes.length === 0 ? (
            <div className="alert alert-info text-center" role="alert" style={{ fontSize: '1.2rem' }}>
              No recipes found.
            </div>
          ) : (
            <Row>
              {myRecipes.map((recipe) => (
                <Col key={recipe.id} md={4} className="mb-4">
                  <Card className="shadow-sm rounded-lg" style={{ backgroundColor: '#fff' }}>
                    <Card.Img
                      variant="top"
                      src={recipe.picture} 
                      alt={recipe.name}
                      style={{ height: '200px', objectFit: 'fill', borderTopLeftRadius: '10px', borderTopRightRadius: '10px' }}
                    />
                    <Card.Body>
                      <Card.Title className="text-uppercase" style={{ fontWeight: '500', color: '#2c3e50' }}>{recipe.name}</Card.Title>
                      <Card.Text className="text-muted" style={{ fontSize: '0.95rem' }}>
                        <strong>Ingredients:</strong> {recipe.ingredients}
                      </Card.Text>
                      <Card.Text className="text-muted" style={{ fontSize: '0.95rem' }}>
                        <strong>Instructions:</strong> {recipe.instructions}
                      </Card.Text>
                      <div className="d-flex justify-content-between align-items-center">
                        <Button variant="success" onClick={() => navigate(`/recipe/edit/${recipe.id}`)} className="btn-sm">
                          Edit
                        </Button>
                        <Button variant="danger" onClick={() => handleDelete(recipe.id)} className="btn-sm">
                          Delete
                        </Button>
                      </div>
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
