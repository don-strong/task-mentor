import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import StudentProfile from './pages/StudentProfile';
import MentorProfile from './pages/MentorProfile';
import TaskCreation from './pages/TaskCreation';
import PublicMentorProfile from './pages/PublicMentorProfile';
import Dashboard from './pages/Dashboard';
import Search from './pages/Search';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          {/* Navigation Bar - Shows on all pages */}
          <Navbar />

          {/* Main Content */}
          <div className="py-6">
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/mentor/:id" element={<PublicMentorProfile />} />
              <Route path="/search" element={<Search />} />

              {/* Protected Student Routes */}
              <Route
                path="/student-profile"
                element={
                  <ProtectedRoute requiredRole="student">
                    <StudentProfile />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute requiredRole="student">
                    <Dashboard />
                  </ProtectedRoute>
                }
              />

              {/* Protected Mentor Routes */}
              <Route
                path="/mentor-profile"
                element={
                  <ProtectedRoute requiredRole="mentor">
                    <MentorProfile />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentor-dashboard"
                element={
                  <ProtectedRoute requiredRole="mentor">
                    <Dashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/task-creation"
                element={
                  <ProtectedRoute requiredRole="mentor">
                    <TaskCreation />
                  </ProtectedRoute>
                }
              />

              {/* Catch all - redirect to home */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </div>
        </div>
      </Router>
    </AuthProvider>
  );
}

// Simple Home Page Component
const Home = () => {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="text-center py-16">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          Welcome to Task Mentor
        </h1>
        <p className="text-xl text-gray-600 mb-8">
          Connect students with professionals through structured, task-based mentorship
        </p>
        <div className="space-x-4">
          <Link
            to="/register"
            className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white px-8 py-3 rounded-md text-lg font-medium transition"
          >
            Get Started
          </Link>
          <Link
            to="/login"
            className="inline-block bg-white hover:bg-gray-50 text-indigo-600 border-2 border-indigo-600 px-8 py-3 rounded-md text-lg font-medium transition"
          >
            Sign In
          </Link>
          <Link
            to="/search"
            className="inline-block bg-gray-100 hover:bg-gray-200 text-gray-900 px-8 py-3 rounded-md text-lg font-medium transition"
          >
            Browse Mentors
          </Link>
        </div>
      </div>
    </div>
  );
};

export default App;