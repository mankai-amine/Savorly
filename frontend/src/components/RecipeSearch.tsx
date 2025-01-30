import { useState } from "react";
import { Recipe,Tag,searchRecipesWithEmbedding } from "../api/RecipeApi"; 
import { Container, Row, Col, Card, Form, Button } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';


const RecipeSearch: React.FC = () => {
    const [query, setQuery] = useState<string>('');
    const [recipes, setRecipes] = useState<Recipe[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const navigate = useNavigate();
  
    const handleSearch = async () => {
      if (!query.trim()) {
        setError('Find a recipe');
        return;
      }

      setLoading(true);
      setError(null);
  
      try {
        const result = await searchRecipesWithEmbedding(query);
        setRecipes(result);
      } catch (err) {
        setError('Failed to search the recipes:' + err);
      } finally {
        setLoading(false);
      }
    };
  
    return (
      <div style={{ minHeight: "100vh" }}>
        <Container className="mt-5">
          <Form className="mb-5 d-flex">
            <Form.Control
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Find a recipe"
              className="me-2"
            />
            <Button style={{ background: "rgb(213, 66, 21)", borderColor: "rgb(213, 66, 21)" }}
                    onClick={handleSearch} disabled={loading}>
              {loading ? "Searching..." : "Search"}
            </Button>
          </Form>
          {error && <p style={{ color: "red" }}>{error}</p>}
          <Row>
            {recipes.map((recipe) => (
              <Col key={recipe.id} md={4} className="mb-4">
                <Card className="shadow-lg rounded" style={{ borderRadius: "10px", overflow: "hidden" }}>
                  <Card.Img variant="top" src={recipe.picture} style={{ height: "200px", objectFit: "fill" }}
                  />
                  <Card.Body>
                    <Card.Title style={{ fontWeight: "bold", fontSize: "1.2rem" }}>{recipe.name}</Card.Title>
                    <Card.Text>
                      <strong>Ingredients:</strong> {recipe.ingredients}
                    </Card.Text>
                    <Card.Text>
                      <strong>Instructions:</strong> {recipe.instructions}
                    </Card.Text>
                    <Button
                      variant="primary"
                      onClick={() => navigate(`/recipe/${recipe.id}`)}
                      style={{ width: '48%' }}
                    >
                      View Recipe
                    </Button>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </Container>
      </div>
    );
  };
  
  export default RecipeSearch;