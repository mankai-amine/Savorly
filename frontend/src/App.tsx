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
import { MyRecipes } from './pages/MyRecipes';
import EditProfile from './pages/EditProfile';
import { Favourites } from './pages/Favourites';
import Tag from './pages/Tag';

import Upload from './pages/Upload';

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
            <Route path='/profile' element={<ProtectedRoute element={EditProfile} />} />
            <Route path='/recipe/add' element={<ProtectedRoute element={AddRecipe} />} />
            <Route path='/recipe/edit/:id' element={<ProtectedRoute element={EditRecipe} />} />
            <Route path='/recipe/myrecipes' element={<ProtectedRoute element={MyRecipes} />} />
            <Route path='/recipe/favourites' element={<ProtectedRoute element={Favourites} />} />
            <Route path="/recipe/:id" element={<RecipeDetails />} />
            <Route path="/upload" element={<Upload />} />
            <Route path="/tags" element={<Tag />} />
          </Routes>
        </Router>
      </div>
    </UserProvider>
  )
}

export default App
