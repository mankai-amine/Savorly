import { useState, useContext } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
import { useForm, SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import axios from 'axios';
import { UserContext } from "../helpers/UserContext";
import { useNavigate } from 'react-router-dom';
import '../Login.css';

const apiUrl = new URL('/api/user', import.meta.env.VITE_API_URL).toString();

const loginSchema = Yup.object().shape({
    username: Yup.string().required('Username is required'),
    password: Yup.string().required('Password is required'),
});

// data structure for form submission
interface LoginFormData {
    username: string;
    password: string;
}

// data structure for login response
interface LoginResponse {
    token: string;
}

// data structure for fetching user
interface UserResponse {
    id: string;
    username: string;
    password:string
}

interface ServerErrors {
    message?: string;
}


const Login = () => {

    const {register, handleSubmit, formState: { errors }} = useForm<LoginFormData>({
        resolver: yupResolver(loginSchema),
    });

    const context = useContext(UserContext);

    if (!context) {
        throw new Error("UserContext must be used within a UserProvider");
    }

    const { setUser } = context;

    const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);

    const [serverErrors, setServerErrors] = useState<ServerErrors>({});

    const navigate = useNavigate();

    const onSubmit: SubmitHandler<LoginFormData>  = async (data: LoginFormData) => {
        setServerErrors({});

        try {
            const response = await axios.post<LoginResponse>(`${apiUrl}/login`, {
                username: data.username,
                password: data.password
            });

            if (response.data.token) {
                sessionStorage.setItem("accessToken", response.data.token);

                const userResponse = await axios.get<UserResponse>(apiUrl, {
                    headers: { Authorization: `Bearer ${response.data.token}` },
                });
                const user = userResponse.data;
                setUser(user);

                setSubmissionStatus('Successfully logged in');
                
                navigate('/');
            }

        } catch (error: any) {
            if (error?.response) {
                setServerErrors(error.response.data);
            } else {
                console.error('Error logging in:', error);
                setSubmissionStatus('Error occurred');
            }
        }
    };

    return (
        <div style={{ minHeight: '90vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Container className='mt-5'>
                <Row className='justify-content-md-center'>
                    <Col md={6} lg={4}>
                        <h2 className='mb-4 text-center'>Log in</h2>
                        <div className='login-box text-center'>
                            <Form onSubmit={handleSubmit(onSubmit)}>
                                <Form.Group controlId='formUsername' className='mb-3'>
                                    <Form.Control
                                        type='username'
                                        placeholder='Enter username'
                                        {...register('username')}
                                    />
                                    {errors.username && <p className="text-danger">{errors.username.message}</p>}
                                </Form.Group>

                                <Form.Group controlId="formPassword" className="mb-3">
                                    <Form.Control
                                        type="password"
                                        placeholder="Enter your password"
                                        {...register('password')}
                                    />
                                    {errors.password && <p className="text-danger">{errors.password.message}</p>}
                                </Form.Group>

                                <Button style={{ background: "rgb(213, 66, 21)", border: 'none' }} type='submit' className='w-100 mb-3'>
                                    Login
                                </Button>
                                
                                {submissionStatus && <p className="text-success">{submissionStatus}</p>}

                                <div className="mt-3">
                                    <p>
                                        Don't have an account? <a href="/register" className="sign-up-link" style={{ color: "rgb(213, 66, 21)" }}>Sign up</a>
                                    </p>
                                </div>
                            </Form>
                            {serverErrors.message && <div className="alert alert-danger mt-3">{serverErrors.message}</div>}
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default Login;