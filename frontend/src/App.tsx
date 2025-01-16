import Login from './pages/Login';
import Register from './pages/Register';
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
          </Routes>
        </Router>
      </div>
    </UserProvider>
  )
}

export default App
