import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import mentorService from '../services/mentorService';
import taskService from '../services/taskService';
import { useAuth } from '../context/AuthContext';

function PublicMentorProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  
  const [mentor, setMentor] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadMentorData();
  }, [id]);

  const loadMentorData = async () => {
    setIsLoading(true);
    try {
      // Load mentor profile
      const mentorData = await mentorService.getMentorById(id);
      setMentor(mentorData);

      // Load mentor's tasks
      const mentorTasks = await taskService.getTasksByMentor(id);
      setTasks(mentorTasks);
    } catch (error) {
      console.error('Error loading mentor data:', error);
      alert('Mentor not found');
      navigate('/');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBookTask = (task) => {
    if (!isAuthenticated) {
      alert('Please login to book a session');
      navigate('/login');
      return;
    }

    if (user?.accountType !== 'student') {
      alert('Only students can book sessions');
      return;
    }

    // TODO: Implement booking modal or navigate to booking page
    alert(`Booking ${task.title}. Booking feature coming soon!`);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading mentor profile...</p>
        </div>
      </div>
    );
  }

  if (!mentor) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600">Mentor not found</p>
        </div>
      </div>
    );
  }

  // Parse comma-separated strings into arrays
  const expertiseAreas = mentor.expertiseAreas 
    ? mentor.expertiseAreas.split(',').map(s => s.trim()).filter(Boolean)
    : [];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header Section */}
      <div className="bg-white shadow">
        <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col sm:flex-row gap-6">
            {/* Profile Image */}
            <div className="shrink-0">
              {mentor.profilePhotoUrl ? (
                <img
                  src={mentor.profilePhotoUrl}
                  alt={mentor.name}
                  className="h-32 w-32 rounded-full object-cover"
                />
              ) : (
                <div className="h-32 w-32 rounded-full bg-indigo-200 flex items-center justify-center text-4xl text-indigo-700 font-bold">
                  {mentor.name.charAt(0)}
                </div>
              )}
            </div>

            {/* Mentor Info */}
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900">{mentor.name}</h1>
              {mentor.roleTitle && mentor.company && (
                <p className="text-xl text-gray-600 mt-1">
                  {mentor.roleTitle} at {mentor.company}
                </p>
              )}
              {mentor.yearsExperience && (
                <p className="text-gray-600 mt-1">{mentor.yearsExperience} years of experience</p>
              )}

              {/* Expertise Tags */}
              {expertiseAreas.length > 0 && (
                <div className="mt-4 flex flex-wrap gap-2">
                  {expertiseAreas.map((area, index) => (
                    <span
                      key={index}
                      className="inline-flex items-center px-3 py-1 rounded-full text-sm bg-indigo-100 text-indigo-800"
                    >
                      {area}
                    </span>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Bio */}
          {mentor.bio && (
            <div className="mt-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-2">About</h2>
              <p className="text-gray-700">{mentor.bio}</p>
            </div>
          )}
        </div>
      </div>

      {/* Tasks Section */}
      <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Available Sessions</h2>

        {tasks.length === 0 ? (
          <div className="bg-white shadow rounded-lg p-6 text-center text-gray-500">
            No sessions available at the moment.
          </div>
        ) : (
          <div className="grid gap-6 md:grid-cols-2">
            {tasks.map((task) => (
              <div key={task.taskId} className="bg-white shadow rounded-lg p-6 hover:shadow-lg transition-shadow">
                <div className="mb-3">
                  <h3 className="text-xl font-semibold text-gray-900">{task.title}</h3>
                </div>

                <p className="text-gray-600 mb-4">{task.description}</p>

                <div className="flex items-center gap-4 text-sm text-gray-600 mb-4">
                  <span className="inline-flex items-center px-3 py-1 rounded-full bg-gray-100 text-gray-800">
                    {task.category}
                  </span>
                  <span>⏱️ {task.durationMinutes} min</span>
                </div>

                <button
                  onClick={() => handleBookTask(task)}
                  className="w-full px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors"
                >
                  Book Session
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default PublicMentorProfile;