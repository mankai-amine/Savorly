import Login from './pages/Login';
import Register from './pages/Register';
import { BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import { UserProvider } from './helpers/UserContext';
import { AddRecipe } from './pages/AddRecipe';
import { Home } from './pages/Home';
import { RecipeDetails } from './pages/RecipeDetails';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js"; 
import Header from './components/Header';
import ProtectedRoute from './helpers/ProtectedRoute';
import { EditRecipe } from './pages/EditRecipe';


function App() {

  return (
    <UserProvider>
      <div>
        <Router>
          <Header />
          <Routes>
            <Route path='/' element={<Home />} />
            <Route path='/login' element={<Login />} />
            <Route path='/register' element={<Register />} />
            <Route path='/recipe/add' element={<ProtectedRoute element={AddRecipe} />} />
            <Route path='/recipe/edit/:id' element={<ProtectedRoute element={EditRecipe} />} />
            <Route path="/recipe/:id" element={<RecipeDetails />} />
          </Routes>
        </Router>
      </div>
    </UserProvider>
  )
}

export default App
