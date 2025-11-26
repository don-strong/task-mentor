import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Home = () => {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Hero Section */}
      <div className="text-center py-16">
        <h1 className="text-5xl font-bold text-gray-900 mb-6">
          Welcome to <span className="text-indigo-600">Task Mentor</span>
        </h1>
        <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
          Connect students with professionals through structured, task-based mentorship.
          Get help with resumes, interviews, career advice, and more.
        </p>

        {!isAuthenticated ? (
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
          </div>
        ) : (
          <div className="space-x-4">
            {user?.accountType === 'student' && (
              <>
                <Link
                  to="/student-profile"
                  className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white px-8 py-3 rounded-md text-lg font-medium transition"
                >
                  My Profile
                </Link>
                <Link
                  to="/discover"
                  className="inline-block bg-white hover:bg-gray-50 text-indigo-600 border-2 border-indigo-600 px-8 py-3 rounded-md text-lg font-medium transition"
                >
                  Find Mentors
                </Link>
              </>
            )}
            {user?.accountType === 'mentor' && (
              <>
                <Link
                  to="/mentor-profile"
                  className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white px-8 py-3 rounded-md text-lg font-medium transition"
                >
                  My Profile
                </Link>
                <Link
                  to="/task-creation"
                  className="inline-block bg-white hover:bg-gray-50 text-indigo-600 border-2 border-indigo-600 px-8 py-3 rounded-md text-lg font-medium transition"
                >
                  Manage Tasks
                </Link>
              </>
            )}
          </div>
        )}
      </div>

      {/* Features Section */}
      <div className="py-16">
        <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
          How It Works
        </h2>
        <div className="grid md:grid-cols-3 gap-8">
          {/* Feature 1 */}
          <div className="text-center">
            <div className="bg-indigo-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-2">Create Your Profile</h3>
            <p className="text-gray-600">
              Students and mentors create profiles highlighting their interests and expertise
            </p>
          </div>

          {/* Feature 2 */}
          <div className="text-center">
            <div className="bg-indigo-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-2">Browse & Connect</h3>
            <p className="text-gray-600">
              Students search for mentors and view their available tasks and services
            </p>
          </div>

          {/* Feature 3 */}
          <div className="text-center">
            <div className="bg-indigo-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-2">Book Sessions</h3>
            <p className="text-gray-600">
              Schedule specific tasks like resume reviews, mock interviews, and career advice
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;