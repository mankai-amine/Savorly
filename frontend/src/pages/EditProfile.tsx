import { useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as Yup from "yup";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Container, Form, Button, Row, Col } from 'react-bootstrap';


const apiUrl = "http://localhost:8080/api/user/edit";

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


    const onSubmit = async (data: ProfileFormData) => {
        try {
            const response = await axios.post(`${apiUrl}`, data);
    
            if (response.status === 200) {
                setIsSubmitted(true);
                navigate("/");
            } else {
                setIsSubmitted(false);
            }
        } catch (error) {
            console.error('Registration error:', error);
        }
    };

    return (
        <div style={{ backgroundColor: '#e3eef8', minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>

            <Container className='mt-5'>
                <Row className='justify-content-md-center'>
                    <Col md={6} lg={4}>
                        <div className='register-box text-center'>
                            <h2 className='mb-4 twixer-logo'> Savorly</h2>
                         
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
                                    Register
                                </Button>

                                {isSubmitted && <p className="text-success">Profile edited successfully</p>}

                                <div className='mt-3'>
                                    <p>Already have an account? <a href='/login' className='login-link'>Login</a></p>
                                </div>
                            </Form>
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default EditProfile;