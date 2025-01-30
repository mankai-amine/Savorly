import { useState } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import UploadPicture from "../components/UploadPicture";

const apiUrl = `${import.meta.env.VITE_API_URL}/recipes`;

const recipeSchema = Yup.object().shape({
    name: Yup.string().min(10, "Name needs to be at least 10 characters").required('Recipe name is required'),
    ingredients: Yup.string().required('Ingredients are required'),
    instructions: Yup.string().required('Instructions are required'),
    picture: Yup.string().default(""),
});

// data structure for form submission
interface AddRecipeFormData {
    name: string;
    ingredients: string;
    instructions: string;
    picture: string;
}

interface ServerErrors {
    message?: string;
}

export const AddRecipe = () => {

    const {register, handleSubmit, setValue, formState: { errors }} = useForm<AddRecipeFormData>({
        resolver: yupResolver(recipeSchema),
    });

    const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
    const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<ServerErrors>({});

    const navigate = useNavigate();

    const handleUploadSuccess = (imgUrl: string) => {
        setValue('picture', imgUrl);
      };

    const onSubmit = async (data: AddRecipeFormData) => {
        setServerErrors({});

        const accessToken = sessionStorage.getItem("accessToken"); 
        if (!accessToken) {
            alert("Authorization token is missing. Please log in again.");
            return;
        }

        try {
            const response = await axios.post(`${apiUrl}/create`, data, {
                headers: {
                    Authorization: `Bearer ${accessToken}`, 
                },
            });
            
            if (response.status === 201) {
                setIsSubmitted(true);
                navigate("/");
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

    return (
        <div style={{ minHeight: '100vh' }}>
          <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
            <Container fluid>
              <Row className="justify-content-center mt-5">
                <Col md={8} lg={6}>
                  <div className='register-box text-center p-4 shadow-lg rounded' style={{ backgroundColor: '#fff' }}>
                    <h2 className='mb-4' style={{ fontSize: '2rem', fontWeight: '600', color: '#343a40' }}>New Recipe</h2>
                    <Form onSubmit={handleSubmit(onSubmit)}>
                      <Form.Group controlId='formName' className='mb-3'>
                        <Form.Control
                          type="text"
                          placeholder="Recipe name"
                          {...register('name')}
                          style={{ padding: '0.75rem', borderRadius: '5px', borderColor: '#ced4da' }}
                        />
                        {errors.name && <p className="text-danger">{errors.name.message}</p>}
                      </Form.Group>
      
                      <Form.Group controlId='formIngredients' className='mb-3'>
                        <Form.Control
                          as="textarea"
                          placeholder="Ingredients"
                          {...register('ingredients')}
                          style={{ padding: '0.75rem', borderRadius: '5px', borderColor: '#ced4da' }}
                        />
                        {errors.ingredients && <p className="text-danger">{errors.ingredients.message}</p>}
                      </Form.Group>
      
                      <Form.Group controlId='formInstructions' className='mb-3'>
                        <Form.Control
                          as="textarea"
                          placeholder="Instructions"
                          {...register('instructions')}
                          style={{ padding: '0.75rem', borderRadius: '5px', borderColor: '#ced4da' }}
                        />
                        {errors.instructions && <p className="text-danger">{errors.instructions.message}</p>}
                      </Form.Group>
      
                      <Form.Group controlId='formPicture' className='mb-3'>
                        <Form.Control
                          type="hidden"
                          {...register('picture')}
                        />
                        {errors.picture && <p className="text-danger">{errors.picture.message}</p>}
                      </Form.Group>
                      <div>
                        <UploadPicture onUploadSuccess={handleUploadSuccess} />
                      </div>
      
                      <Button
                        variant='primary'
                        type='submit'
                        className='w-100 mt-2 mb-3'
                        style={{ backgroundColor: '#007bff', borderColor: '#007bff', padding: '0.75rem' }}
                      >
                        Add Recipe
                      </Button>
      
                      {isSubmitted && <p className="text-success">Recipe added successfully!</p>}
                    </Form>
                  </div>
                </Col>
              </Row>
            </Container>
          </div>
        </div>
    );
      
};
