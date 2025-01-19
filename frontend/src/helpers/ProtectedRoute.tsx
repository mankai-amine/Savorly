import { Navigate } from "react-router-dom";
import { ReactElement, ComponentType } from "react";

interface ProtectedRouteProps {
    // Type for the component to render
    element: ComponentType<any>; 
    // To allow passing additional props
    [key: string]: any; 
}

const ProtectedRoute = ({ element: Component, ...rest }: ProtectedRouteProps): ReactElement => {
  const accessToken = sessionStorage.getItem("accessToken");

  // If the user is authenticated, render the component; otherwise, redirect to login
  return accessToken ? <Component {...rest} /> : <Navigate to="/login" />;
};

export default ProtectedRoute;
