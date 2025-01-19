import { useState, useContext } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
import { useForm, SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import axios from 'axios';
import { UserContext } from "../helpers/UserContext"
import { useNavigate } from 'react-router-dom';

const apiUrl = "http://localhost:8080/api/recipe";

const recipeSchema = Yup.object().shape({
    name: Yup.string().min(5, "Name needs to be at least 5 characters").required('Recipe name is required'),
    ingredients: Yup.string().required('Ingredients are required'),
    instructions: Yup.string().required('Instructions are required'),
});

// data structure for form submission
interface AddRecipeFormData {
    name: string;
    ingredients: string;
    instructions: string;
}

interface ServerErrors {
    message?: string;
}

export const AddRecipe = () => {

    const {register, handleSubmit, formState: { errors }} = useForm<AddRecipeFormData>({
        resolver: yupResolver(recipeSchema),
    });

    const context = useContext(UserContext);

    if (!context) {
        throw new Error("UserContext must be used within a UserProvider");
    }

    const { user } = context;

    const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
    const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<ServerErrors>({});

    const navigate = useNavigate();

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
        <div style={{ backgroundColor: '#e3eef8', minHeight: '100vh' }}>
            <div className="d-flex">
                <Container fluid>
                    <Row className="justify-content-center mt-5">
                        <Col md={8} lg={6}>
                            <div className='register-box text-center'>
                                <h2 className='mb-4'>New recipe</h2>
                                <Form onSubmit={handleSubmit(onSubmit)}>
                                    <Form.Group controlId='formName' className='mb-3'>
                                        <Form.Control
                                            type="text"
                                            placeholder="Recipe name"
                                            {...register('name')}
                                        />
                                        {errors.name && <p className="text-danger">{errors.name.message}</p>}
                                    </Form.Group>
                                    
                                    <Form.Group controlId='formIngredients' className='mb-3'>
                                        <Form.Control
                                            as="textarea"
                                            placeholder="Ingredients"
                                            {...register('ingredients')}
                                        />
                                        {errors.ingredients && <p className="text-danger">{errors.ingredients.message}</p>}
                                    </Form.Group>
                                    
                                    <Form.Group controlId='formInstructions' className='mb-3'>
                                        <Form.Control
                                            as="textarea"
                                            placeholder="Instructions"
                                            {...register('instructions')}
                                        />
                                        {errors.instructions && <p className="text-danger">{errors.instructions.message}</p>}
                                    </Form.Group>

                                    <Button variant='primary' type='submit' className='w-100 mb-3'>
                                        Add recipe
                                    </Button>

                                    {isSubmitted && <p className="text-success">Recipe added</p>}
                                </Form>
                            </div>
                        </Col>
                    </Row>
                </Container>
            </div>
        </div>
    );
};
