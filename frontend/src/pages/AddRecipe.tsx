import { useState, useContext } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
import { useForm, SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import UploadPicture from "../components/UploadPicture";

const apiUrl = "http://localhost:8080/api/recipes";

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

    const {register, handleSubmit, formState: { errors }} = useForm<AddRecipeFormData>({
        resolver: yupResolver(recipeSchema),
    });

    const [pictureUrl, setPictureUrl] = useState<string>("");
    const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
    const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<ServerErrors>({});

    const navigate = useNavigate();

    const handleUploadSuccess = (imgUrl: string) => {
        setPictureUrl(imgUrl)
      };

    const onSubmit = async (data: AddRecipeFormData) => {
        setServerErrors({});

        const accessToken = sessionStorage.getItem("accessToken"); 
        if (!accessToken) {
            alert("Authorization token is missing. Please log in again.");
            return;
        }

        try {
            const response = await axios.post(`${apiUrl}/new`, data, {
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

                                    <Form.Group controlId='formPicture' className='mb-3'>
                                        <Form.Control
                                            as="text"
                                            {...register('picture')}
                                        />
                                        {errors.picture && <p className="text-danger">{errors.picture.message}</p>}
                                    </Form.Group>
                                    <div>
                                        {/* pass as call back*/}
                                        <UploadPicture onUploadSuccess={handleUploadSuccess} />
                                    </div>

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
