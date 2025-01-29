import { useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Form, Button, Row, Col } from 'react-bootstrap';
import UploadPicture from '../components/UploadPicture';


const apiUrl = "http://localhost:8080/api/recipes";

const recipeSchema = Yup.object().shape({
    name: Yup.string().min(10, "Name needs to be at least 10 characters").required('Recipe name is required'),
    ingredients: Yup.string().required('Ingredients are required'),
    instructions: Yup.string().required('Instructions are required'),
    picture: Yup.string().default(""),
});

// data structure for form submission
interface EditRecipeFormData {
    name: string;
    ingredients: string;
    instructions: string;
    picture: string;
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

    const handleUploadSuccess = (imgUrl: string) => {
        setValue('picture', imgUrl);
      };


    const onSubmit = async (data: EditRecipeFormData) => {
        const accessToken = sessionStorage.getItem("accessToken");

        try {
            const response = await axios.put(`${apiUrl}/edit/${id}`, data, {
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
        <div style={{ minHeight: '100vh' }}>
          <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
            <Container fluid>
              <Row className="justify-content-center mt-5">
                <Col md={8} lg={6}>
                  <div className='register-box text-center p-4 shadow-lg rounded' style={{ backgroundColor: '#fff' }}>
                    <h2 className='mb-4' style={{ fontSize: '2rem', fontWeight: '600', color: '#343a40' }}>Edit Recipe</h2>
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
                        className='w-100 mb-3'
                        style={{ backgroundColor: '#007bff', borderColor: '#007bff', padding: '0.75rem' }}
                      >
                        Edit Recipe
                      </Button>
      
                      {isSubmitted && <p className="text-success">Recipe edited successfully!</p>}
                    </Form>
                  </div>
                </Col>
              </Row>
            </Container>
          </div>
        </div>
    );
      
};

