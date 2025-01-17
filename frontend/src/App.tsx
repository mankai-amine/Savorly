import Login from './pages/Login';
import Register from './pages/Register';
import { BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import { UserProvider } from './helpers/UserContext';
import { AddRecipe } from './pages/AddRecipe';
import { Home } from './pages/Home';
import { RecipeDetails } from './pages/RecipeDetails';

function App() {

  return (
    <UserProvider>
      <div>
        <Router>
          <Routes>
            <Route path='/' element={<Home />} />
            <Route path='/login' element={<Login />} />
            <Route path='/register' element={<Register />} />
            <Route path='/recipe/add' element={<AddRecipe />} />
            <Route path="/recipe/:id" element={<RecipeDetails />} />
          </Routes>
        </Router>
      </div>
    </UserProvider>
  )
}

export default App
