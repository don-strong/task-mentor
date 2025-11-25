import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Register from './components/Register';
import Login from './components/Login';
import StudentProfile from './components/StudentProfile';
import MentorProfile from './components/MentorProfile';
import TaskCreation from './components/TaskCreation';
import PublicMentorProfile from './components/PublicMentorProfile';

function Home() {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          <h1 className="text-4xl font-extrabold text-gray-900 sm:text-5xl sm:tracking-tight lg:text-6xl">
            Welcome to Task Mentor
          </h1>
          <p className="mt-5 max-w-xl mx-auto text-xl text-gray-500">
            Connect with professionals through structured, task-based mentorship
          </p>
          <div className="mt-10 flex justify-center gap-4">
            <Link
              to="/register"
              className="px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 md:py-4 md:text-lg md:px-10"
            >
              Get Started
            </Link>
            <Link
              to="/login"
              className="px-8 py-3 border border-transparent text-base font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 md:py-4 md:text-lg md:px-10"
            >
              Sign In
            </Link>
          </div>
        </div>

        {/* Demo Links Section */}
        <div className="mt-20">
          <h2 className="text-2xl font-bold text-gray-900 text-center mb-8">
            Component Demo Links
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <DemoCard
              title="Registration"
              description="User registration form for students and mentors"
              link="/register"
            />
            <DemoCard
              title="Login"
              description="Authentication page for returning users"
              link="/login"
            />
            <DemoCard
              title="Student Profile"
              description="Student profile management interface"
              link="/student-profile"
            />
            <DemoCard
              title="Mentor Profile"
              description="Mentor profile management interface"
              link="/mentor-profile"
            />
            <DemoCard
              title="Task Creation"
              description="Mentor task menu management"
              link="/task-creation"
            />
            <DemoCard
              title="Public Mentor Profile"
              description="Public-facing mentor profile with available tasks"
              link="/mentor/1"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function DemoCard({ title, description, link }) {
  return (
    <Link
      to={link}
      className="block bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow p-6"
    >
      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-gray-600 text-sm">{description}</p>
      <div className="mt-4 text-indigo-600 text-sm font-medium">View →</div>
    </Link>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          
          {/* Protected Routes */}
          <Route 
            path="/student-profile" 
            element={
              <ProtectedRoute requiredRole="student">
                <StudentProfile />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/mentor-profile" 
            element={
              <ProtectedRoute requiredRole="mentor">
                <MentorProfile />
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
          
          {/* Public Routes */}
          <Route path="/mentor/:id" element={<PublicMentorProfile />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;