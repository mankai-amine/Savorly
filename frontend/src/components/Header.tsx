import { useContext } from "react";
import { Link } from "react-router-dom";
import { UserContext } from "../helpers/UserContext";
import { Navbar, Nav, Button, Container } from "react-bootstrap";

const Header = () => {
  const userContext = useContext(UserContext);

  if (!userContext) {
    throw new Error("UserContext is undefined");
  }

  const { user } = userContext;

  return (
    <Navbar expand="lg" className="shadow-sm py-2">
      <Container>
        
        <Navbar.Brand
          as={Link}
          to="/"
          style={{ color: "rgb(213, 66, 21)", fontWeight: "bold", fontSize: "1.5rem" }}
        >
          Savorly
        </Navbar.Brand>
  
        {/* Toggle Button for Small Screens */}
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
  
        {/* Collapsible Menu */}
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto align-items-center">

            <Nav.Item className="me-3">
              <span className="navbar-text text-secondary">
                Welcome, {user ? user.username : "Guest"}!
              </span>
            </Nav.Item>
  
            <Nav.Link as={Link} to="/" className="text-dark nav-item-hover">
              Home
            </Nav.Link>
  
            {user ? (
              <>
                <Nav.Link as={Link} to="/recipe/add" className="text-dark nav-item-hover">
                  Create Recipe
                </Nav.Link>
                <Nav.Link as={Link} to="/recipe/myrecipes" className="text-dark nav-item-hover">
                  My Recipes
                </Nav.Link>
                <Nav.Link as={Link} to="/recipe/favourites" className="text-dark nav-item-hover">
                  Favourites
                </Nav.Link>
                <Nav.Link as={Link} to="/profile" className="text-dark nav-item-hover">
                  Edit Profile
                </Nav.Link>
                <Button
                  variant="outline-danger"
                  size="sm"
                  className="ms-2"
                  onClick={() => {
                    sessionStorage.removeItem("accessToken");
                    window.location.reload();
                  }}
                >
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login" className="text-dark nav-item-hover">
                  Login
                </Nav.Link>
                <Nav.Link as={Link} to="/register" className="text-dark nav-item-hover">
                  Register
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
      
    </Navbar>
  );
  
  
};

export default Header;
