import { useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Form, Button, Row, Col } from 'react-bootstrap';


const apiUrl = "http://localhost:8080/api/recipes";

const recipeSchema = Yup.object().shape({
    name: Yup.string().min(10, "Name needs to be at least 10 characters").required('Recipe name is required'),
    ingredients: Yup.string().required('Ingredients are required'),
    instructions: Yup.string().required('Instructions are required'),
});

// data structure for form submission
interface EditRecipeFormData {
    name: string;
    ingredients: string;
    instructions: string;
}

interface ServerErrors {
    message?: string;
}

export const EditRecipe = () => {
    
    const {register, handleSubmit, setValue, formState: { errors }} = useForm<EditRecipeFormData>({
        resolver: yupResolver(recipeSchema),
    });

    const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
    const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);
    const [serverErrors, setServerErrors] = useState<ServerErrors>({});
    const [recipe, setRecipe] = useState<EditRecipeFormData | null>(null);

    const navigate = useNavigate();
    const { id } = useParams();

    // Fetch recipe data on component mount or when the `id` changes
    useEffect(() => {
        const fetchRecipe = async () => {
        try {
            const response = await axios.get<EditRecipeFormData>(`${apiUrl}/${id}`);
            setRecipe(response.data); 
        } catch (error) {
            console.error("Error fetching recipe data:", error);
        }
        };

        fetchRecipe();
    }, [id]);

    useEffect(() => {
        if (recipe) {
        // Populate form values once the recipe data is fetched
        setValue("name", recipe.name);
        setValue("ingredients", recipe.ingredients);
        setValue("instructions", recipe.instructions);
        }
    }, [recipe, setValue]);


    const onSubmit = async (data: EditRecipeFormData) => {
        const accessToken = sessionStorage.getItem("accessToken");

        try {
            const response = await axios.put(`${apiUrl}/update/${id}`, data, {
                headers: {
                    Authorization: `Bearer ${accessToken}`, 
                },
            });
            
            if (response.status === 200) {
                setIsSubmitted(true);
                navigate("/");
            } else {
                setIsSubmitted(false);
                alert("Editing recipe failed. Please try again.");
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
                                        Edit recipe
                                    </Button>

                                    {isSubmitted && <p className="text-success">Recipe edited</p>}
                                </Form>
                            </div>
                        </Col>
                    </Row>
                </Container>
            </div>
        </div>
    );
};

