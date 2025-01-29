import { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const apiUrl = 'http://localhost:8080/api/recipes';
const favouritesApiUrl = 'http://localhost:8080/api/favourites';


interface Recipe {
  id: number;
  name: string;
  ingredients: string;
  instructions: string;
  picture: string;
  authorId: number;
}

// TODO ADD THE SEARCH FUNCTIONALITY PLUGIN (RECIPESEARCH.TSX)

export const Home = () => {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  // Stores IDs of favourite recipes
  const [favourites, setFavourites] = useState<number[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const accessToken = sessionStorage.getItem("accessToken");
  if (!accessToken) {
    navigate("/login")
  }

  useEffect(() => {
    const fetchData  = async () => {
      try {
        const [recipesResponse, favouritesResponse] = await Promise.all([
          axios.get(`${apiUrl}/all`, {
            headers: { Authorization: `Bearer ${accessToken}` },
          }),
          // Get favorite recipe IDs
          axios.get<Recipe[]>(favouritesApiUrl, {
            headers: { Authorization: `Bearer ${accessToken}` },
            }),
        ]);
        setRecipes(recipesResponse.data as Recipe[]);
        setFavourites(favouritesResponse.data.map((recipe) => recipe.id));
      } catch (err) {
        setError('Failed to load recipes');
      } finally {
        setLoading(false);
      }
    };

    fetchData ();
  }, []);

  const toggleFavourite = async (recipeId: number) => {
    try {
      if (favourites.includes(recipeId)) {
        await axios.delete(`${favouritesApiUrl}/${recipeId}`, {
          headers: { Authorization: `Bearer ${accessToken}` },
        });
        setFavourites(favourites.filter((id) => id !== recipeId));
      } else {
        await axios.post(`${favouritesApiUrl}/${recipeId}`, null, {
          headers: { Authorization: `Bearer ${accessToken}` },
        });
        setFavourites([...favourites, recipeId]);
      }
    } catch (err) {
      console.error('Failed to update favourites:', err);
      alert('Error updating favourites.');
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div style={{ minHeight: '100vh' }}>
      <Container className="mt-5">
        <Row >
          {recipes.map((recipe) => (
            <Col key={recipe.id} md={4} className="mb-4 ">
              <Card className="shadow-lg rounded" style={{ borderRadius: '10px', overflow: 'hidden' }}>
                <Card.Img variant="top" src={recipe.picture} style={{ height: '200px', objectFit: 'fill' }} />
                <Card.Body>
                  <Card.Title style={{ fontWeight: 'bold', fontSize: '1.2rem' }}>{recipe.name}</Card.Title>
                  <Card.Text>
                    <strong>Ingredients:</strong> {recipe.ingredients}
                  </Card.Text>
                  <Card.Text>
                    <strong>Instructions:</strong> {recipe.instructions}
                  </Card.Text>
                  <div className="d-flex justify-content-between align-items-center">
                    <Button
                      variant="primary"
                      onClick={() => navigate(`/recipe/${recipe.id}`)}
                      style={{ width: '48%' }}
                    >
                      View Recipe
                    </Button>
                    <Button
                      variant={favourites.includes(recipe.id) ? 'danger' : 'success'}
                      style={{ marginLeft: '10px', width: '48%' }}
                      onClick={() => toggleFavourite(recipe.id)}
                    >
                      {favourites.includes(recipe.id) ? 'Remove Favourite' : 'Add to Favourites'}
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>
    </div>
  );
};
