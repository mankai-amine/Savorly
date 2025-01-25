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
    <Navbar bg="light" expand="lg" >
      <Container>

        <Navbar.Brand as={Link} to="/">
          Savorly
        </Navbar.Brand>

        {/* Toggle Button for Small Screens */}
        <Navbar.Toggle aria-controls="basic-navbar-nav" />

        {/* Collapsible Menu */}
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            
            <Nav.Item className="d-flex align-items-center me-3">
              <span className="navbar-text">
                Welcome, {user ? user.username : "Guest"}!
              </span>
            </Nav.Item>

            <Nav.Link as={Link} to="/">
              Home
            </Nav.Link>

            {user ? (
              <>
                <Nav.Link as={Link} to="/recipe/add">
                  Create Recipe
                </Nav.Link>
                <Nav.Link as={Link} to="/recipe/myrecipes">
                  My Recipes
                </Nav.Link>
                <Nav.Link as={Link} to="/recipe/favourites">
                  Favourites
                </Nav.Link>
                <Nav.Link as={Link} to="/profile">
                  Edit Profile
                </Nav.Link>
                <Button
                  variant="outline-danger"
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
                <Nav.Link as={Link} to="/login">
                  Login
                </Nav.Link>
                <Nav.Link as={Link} to="/register">
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
