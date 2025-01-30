import { useContext, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Container, Card, Button, Row, Col, Form } from 'react-bootstrap';
import * as Yup from 'yup';
import axios from 'axios';
import { yupResolver } from '@hookform/resolvers/yup';
import { useForm } from 'react-hook-form';
import { UserContext } from '../helpers/UserContext';
import StarRating from '../components/StarRating';

const apiUrl = 'http://localhost:8080/api/recipes'; 
const reviewsUrl = 'http://localhost:8080/api/reviews'; 
const ratingUrl = 'http://localhost:8080/api/rating/recipe';

interface User {
  username: string;
}

interface Review {
  id:number;
  text: string;
  author: User;
  date: string
}

const reviewSchema = Yup.object().shape({
    text: Yup.string().min(1, "Text needs to be at least 1 character").required('Text is required'),
});

interface AddReviewFormData {
  text: string;
}

interface ServerErrors {
  message?: string;
}

export const RecipeDetails = () => {
  const { id } = useParams();
  const [recipe, setRecipe] = useState<any>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [rating, setRating] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const getAvgRating = async () => {
    try {
      const response = await axios.get(`${ratingUrl}/${id}`);
      setRating(response.data);
    } catch (err) {
      console.error('Failed to fetch average rating');
    }
  };

  useEffect(() => {
    const fetchRecipe = async () => {
      try {
        const response = await axios.get(`${apiUrl}/${id}`);
        const response2 = await axios.get<Review[]>(`${reviewsUrl}/${id}`);
        const response3 = await axios.get(`${ratingUrl}/${id}`);
        setRating(response3.data);
        setRecipe(response.data);
        console.log(response2.data);
        setReviews(response2.data as Review[]);
      } catch (err) {
        setError('Failed to load recipe details');
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchRecipe();
  }, [id]);

  const userContext = useContext(UserContext);
      
  if (!userContext) {
  throw new Error("UserContext is undefined");
  }
  
  const { user } = userContext;

  // Adding a review
  const {register, handleSubmit, formState: { errors }} = useForm<AddReviewFormData>({
    resolver: yupResolver(reviewSchema),
  });

  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
  const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);
  const [serverErrors, setServerErrors] = useState<ServerErrors>({});

  const onSubmit = async (data: AddReviewFormData) => {
    setServerErrors({});

    try {
      const accessToken = sessionStorage.getItem("accessToken"); 
      if (!accessToken) {
          alert("Authorization token is missing. Please log in.");
          return;
      } 

      const response = await axios.post(`${reviewsUrl}/${id}`, data.text, {
        headers: {
            Authorization: `Bearer ${accessToken}`, 
            // next line ensure that the text gets sent as it is (as a raw text), without being defaulted to application/x-www-form-urlencoded
            "Content-Type": "text/plain",
        },
      });
      
      if (response.status === 201) {
          setIsSubmitted(true);
          window.location.reload();
      } else {
          setIsSubmitted(false);
          alert("Adding recipe failed. Please try again.");
      }

    } catch (error: any) {
        if (error?.response) {
            setServerErrors(error.response.data);
        } else {
            console.error('Error posting:', error);
            setSubmissionStatus('Error occurred');
        }
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <Container className="mt-5">
      {/* Recipe Title and Header */}
      <h1 className="text-center mb-5" 
        style={{ fontSize: '2rem', fontWeight: 'bold', color: 'rgb(213, 66, 21)', letterSpacing: '2px', textShadow: '2px 2px 4px rgba(0, 0, 0, 0.2)' }}>
        {recipe.name}
      </h1>
      
      {/* Recipe Card */}
      <Card className="shadow-lg mb-4 p-4">
        <Card.Body>
          <div className="row align-items-center">
            {/* Left side: Ingredients, Instructions, Rating */}
            <div className="col-md-8">
              <Card.Title className="text-uppercase text-secondary">Ingredients</Card.Title>
              <Card.Text>{recipe.ingredients}</Card.Text>
              
              <Card.Title className="text-uppercase text-secondary">Instructions</Card.Title>
              <Card.Text>{recipe.instructions}</Card.Text>
              
              <Card.Title className="text-uppercase text-secondary">Average Rating</Card.Title>
              {/* <Card.Text><strong>{rating}</strong></Card.Text> */}
            </div>

            {/* Right side: Recipe Image */}
            <div className="col-md-4">
              <img
                src={recipe.picture} // Assuming recipe.picture is the URL of the image
                alt={recipe.name}
                className="img-fluid rounded"
                style={{ maxHeight: '200px', objectFit: 'cover' }}
              />
            </div>
          </div>

          {/* Star Rating Component */}
          {user && id && (
            <StarRating 
              recipeId={id} 
              initialRating={rating} 
              onRatingSubmit={getAvgRating}
            />
          )}

          {/* Print PDF Button */}
          <Button variant="outline-primary" className="mt-3">
            <Link to={`/recipe/pdf/${id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
              <strong>Print PDF</strong>
            </Link>
          </Button>
        </Card.Body>
      </Card>

      {/* Reviews Section */}
      <h2 className="mb-4 mt-5">Reviews</h2>
      {Array.isArray(reviews) && reviews.length === 0 ? (
        <p className="text-center">No reviews yet.</p>
      ) : (
        <Row>
          {reviews.map((review) => (
            <Col key={review.id} md={4} className="mb-4">
              <Card className="shadow-sm">
                <Card.Body>
                  <Card.Text>
                    <strong>{review.author.username}</strong> wrote on {review.date}:
                  </Card.Text>
                  <Card.Text>{review.text}</Card.Text>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      {/* Comment Form (if user is logged in) */}
      {user && (
        <Form onSubmit={handleSubmit(onSubmit)}>
          <Form.Group controlId="formText" className="mb-3">
            <Form.Control
              type="text"
              placeholder="Write your comment here..."
              {...register("text")}
            />
            {errors.text && <p className="text-danger">{errors.text.message}</p>}
          </Form.Group>

          <Button variant="primary" type="submit" className="mb-3">
            Comment
          </Button>

          {isSubmitted && <p className="text-success">Review added</p>}
        </Form>
      )}
    </Container>
  );
};
