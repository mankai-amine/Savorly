import Login from './pages/Login';
import Register from './pages/Register';
import Tag from './pages/Tag';
import Recipe from './pages/Recipes/Recipe';
import CreateRecipe from './pages/Recipes/CreateRecipe';
import EditRecipe from './pages/Recipes/EditRecipe';
import { BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import { UserProvider } from './helpers/UserContext';

function App() {

  return (
    <UserProvider>
      <div className='App'>
        <Router>
          <Routes>
            <Route path='/login' element={<Login />} />
            <Route path='/register' element={<Register />} />
            <Route path="/recipes" element={<Recipe />} />
            <Route path="/recipes/new" element={<CreateRecipe />} />
            <Route path="/recipes/edit/:id" element={<EditRecipe />} />
            <Route path="/tags" element={<Tag />} />
          </Routes>
        </Router>
      </div>
    </UserProvider>
  )
}

export default App
