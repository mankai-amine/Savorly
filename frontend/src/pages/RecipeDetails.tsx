import { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
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
      <h1 className="mb-4">{recipe.name}</h1>
      <Card className="mb-4">
        <Card.Body>
          <Card.Title>Ingredients</Card.Title>
          <Card.Text>{recipe.ingredients}</Card.Text>
          <Card.Title>Instructions</Card.Title>
          <Card.Text>{recipe.instructions}</Card.Text>
          <Card.Title>Average Rating</Card.Title>
          <Card.Text>{rating}</Card.Text>
          {user && id && <StarRating 
                          recipeId={id} 
                          initialRating={rating} 
                          onRatingSubmit={(newRating) => {
                            setRating(newRating);
                          }}
/>}
          <Button variant="secondary" href="/">Back to Home</Button>
        </Card.Body>
      </Card>
      
      <h2 className="mb-4">Reviews</h2>
      {Array.isArray(reviews) && reviews.length === 0 ? (
        <p>
          No reviews yet.
        </p>
        ) : (
      <Row>
        {reviews.map((review) => (
          <Row key={review.id} md={4} className="mb-4">
              <Card.Body>
                <Card.Text>
                  <strong>{review.author.username}</strong> wrote on {review.date}:
                </Card.Text>
                <Card.Text>
                  {review.text}
                </Card.Text>
                <hr />
              </Card.Body>
            
          </Row>
        ))}
      </Row>
      )}

      {user &&
      <Form onSubmit={handleSubmit(onSubmit)}>
          <Form.Group controlId='formText' className='mb-3'>
              <Form.Control
                  type="text"
                  placeholder="Write your comment here..."
                  {...register('text')}
              />
              {errors.text && <p className="text-danger">{errors.text.message}</p>}
          </Form.Group>

          <Button variant='primary' type='submit' className='mb-3'>
              Comment
          </Button>

          {isSubmitted && <p className="text-success">Review added</p>}
      </Form>
      }   
    </Container>
  );
};
