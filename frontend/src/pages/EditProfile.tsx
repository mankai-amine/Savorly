import { useState, useContext } from 'react';
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../helpers/UserContext";
import { Container, Form, Button, Row, Col } from 'react-bootstrap';


const apiUrl = `${import.meta.env.VITE_API_URL}/user/edit`;

const profileSchema = Yup.object().shape({
    password: Yup.string().required('Password is required').min(6, 'Password must be at least 6 characters'),
    password2: Yup.string()
        .oneOf([Yup.ref('password'), undefined], 'Passwords must match')
        .required('Confirm Password is required'),

});

interface ProfileFormData {
    password: string;
    password2: string;
}

const EditProfile = () => {
    const [isSubmitted, setIsSubmitted] = useState<boolean>(false);

    const { register, handleSubmit, formState: { errors } } = useForm<ProfileFormData>({
        resolver: yupResolver(profileSchema),
    });

    const navigate = useNavigate();

    const accessToken = sessionStorage.getItem("accessToken"); 
    if (!accessToken) {
        alert("Authorization token is missing. Please log in again.");
        return;
    }

    const userContext = useContext(UserContext);
    
    if (!userContext) {
    throw new Error("UserContext is undefined");
    }

    const onSubmit = async (data: ProfileFormData) => {
        try {
            const response = await axios.post(`${apiUrl}`, data, {
                headers: {
                    Authorization: `Bearer ${accessToken}`, 
                },
            });
    
            if (response.status === 200) {
                setIsSubmitted(true);
                navigate("/");
            } else {
                setIsSubmitted(false);
                alert("Editing profile failed. Please try again.");
            }
        } catch (error) {
            console.error('Registration error:', error);
        }
    };

    return (
        <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>

            <Container >
                <Row className='justify-content-md-center'>
                    <Col md={6} lg={4}>
                    <h2 className='mb-4 text-center'> Change password</h2>
                        <div className='register-box text-center '>                           
                            <Form onSubmit={handleSubmit(onSubmit)}>
                                <Form.Group controlId="formPassword" className="mb-3">
                                    <Form.Control
                                        type="password"
                                        placeholder="Enter your new password"
                                        {...register('password')}
                                    />
                                    {errors.password && <p className="text-danger">{errors.password.message}</p>}
                                </Form.Group>
                                <Form.Group controlId="formConfirmPassword" className="mb-3">
                                    <Form.Control
                                        type="password"
                                        placeholder="Confirm your new password"
                                        {...register('password2')}
                                    />
                                    {errors.password2 && <p className="text-danger">{errors.password2.message}</p>}
                                </Form.Group>

                                <Button variant='primary' type='submit' className='w-100 mb-3'>
                                    Edit password
                                </Button>

                                {isSubmitted && <p className="text-success">Profile edited successfully</p>}
                            </Form>
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default EditProfile;