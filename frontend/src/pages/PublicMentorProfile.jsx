import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import mentorService from '../services/mentorService';
import taskService from '../services/taskService';
import bookingService from '../services/bookingService';
import studentService from '../services/studentService';
import { useAuth } from '../context/AuthContext';

function PublicMentorProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  
  const [mentor, setMentor] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // Booking modal state
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [proposedDatetime, setProposedDatetime] = useState('');
  const [bookingLoading, setBookingLoading] = useState(false);
  const [bookingError, setBookingError] = useState(null);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  
  // Student profile state (needed for studentId)
  const [studentProfile, setStudentProfile] = useState(null);

  useEffect(() => {
    loadMentorData();
  }, [id]);

  useEffect(() => {
    // Load student profile if user is a student
    if (isAuthenticated && user?.accountType === 'student') {
      loadStudentProfile();
    }
  }, [isAuthenticated, user]);

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

  const loadStudentProfile = async () => {
    try {
      const profile = await studentService.getMyProfile();
      setStudentProfile(profile);
    } catch (error) {
      console.error('Error loading student profile:', error);
      // Student might not have created a profile yet
      setStudentProfile(null);
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

    if (!studentProfile) {
      alert('Please create your student profile first before booking sessions.');
      navigate('/student/profile');
      return;
    }

    // Open booking modal
    setSelectedTask(task);
    setProposedDatetime('');
    setBookingError(null);
    setBookingSuccess(false);
    setShowBookingModal(true);
  };

  const handleCloseModal = () => {
    setShowBookingModal(false);
    setSelectedTask(null);
    setProposedDatetime('');
    setBookingError(null);
    setBookingSuccess(false);
  };

  const handleSubmitBooking = async (e) => {
    e.preventDefault();
    
    if (!proposedDatetime) {
      setBookingError('Please select a date and time');
      return;
    }

    setBookingLoading(true);
    setBookingError(null);

    try {
      const bookingData = {
        studentId: studentProfile.studentId,
        mentorId: parseInt(id),
        taskId: selectedTask.taskId,
        proposedDatetime: proposedDatetime
      };

      await bookingService.createBooking(bookingData);
      setBookingSuccess(true);
      
      // Close modal after 2 seconds
      setTimeout(() => {
        handleCloseModal();
      }, 2000);
    } catch (error) {
      console.error('Booking error:', error);
      setBookingError(error?.error || error?.message || 'Failed to create booking. Please try again.');
    } finally {
      setBookingLoading(false);
    }
  };

  // Get minimum datetime (now + 1 hour)
  const getMinDatetime = () => {
    const now = new Date();
    now.setHours(now.getHours() + 1);
    return now.toISOString().slice(0, 16);
  };

  // Get maximum datetime (6 months from now)
  const getMaxDatetime = () => {
    const maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 6);
    return maxDate.toISOString().slice(0, 16);
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

      {/* Booking Modal */}
      {showBookingModal && selectedTask && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            {/* Modal Header */}
            <div className="px-6 py-4 border-b border-gray-200">
              <div className="flex justify-between items-center">
                <h2 className="text-xl font-bold text-gray-900">Book Session</h2>
                <button
                  onClick={handleCloseModal}
                  className="text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>

            {/* Modal Body */}
            <div className="px-6 py-4">
              {/* Success Message */}
              {bookingSuccess ? (
                <div className="text-center py-8">
                  <div className="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-green-100 mb-4">
                    <svg className="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">Booking Request Sent!</h3>
                  <p className="text-gray-600">
                    Your booking request has been sent to {mentor.name}. You'll be notified when they respond.
                  </p>
                </div>
              ) : (
                <>
                  {/* Session Details */}
                  <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                    <h3 className="font-medium text-gray-900 mb-2">{selectedTask.title}</h3>
                    <div className="text-sm text-gray-600 space-y-1">
                      <p><span className="font-medium">Mentor:</span> {mentor.name}</p>
                      <p><span className="font-medium">Duration:</span> {selectedTask.durationMinutes} minutes</p>
                      <p><span className="font-medium">Category:</span> {selectedTask.category}</p>
                    </div>
                  </div>

                  {/* Error Message */}
                  {bookingError && (
                    <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                      {bookingError}
                    </div>
                  )}

                  {/* Booking Form */}
                  <form onSubmit={handleSubmitBooking}>
                    <div className="mb-6">
                      <label htmlFor="proposedDatetime" className="block text-sm font-medium text-gray-700 mb-2">
                        Proposed Date & Time *
                      </label>
                      <input
                        type="datetime-local"
                        id="proposedDatetime"
                        value={proposedDatetime}
                        onChange={(e) => setProposedDatetime(e.target.value)}
                        min={getMinDatetime()}
                        max={getMaxDatetime()}
                        required
                        className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                      />
                      <p className="mt-1 text-xs text-gray-500">
                        Must be at least 1 hour from now and within 6 months
                      </p>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={handleCloseModal}
                        disabled={bookingLoading}
                        className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500 disabled:opacity-50 transition-colors"
                      >
                        Cancel
                      </button>
                      <button
                        type="submit"
                        disabled={bookingLoading}
                        className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50 transition-colors"
                      >
                        {bookingLoading ? (
                          <span className="flex items-center justify-center">
                            <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Booking...
                          </span>
                        ) : (
                          'Confirm Booking'
                        )}
                      </button>
                    </div>
                  </form>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default PublicMentorProfile;