import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import bookingService from '../services/bookingService';
import taskService from '../services/taskService';
import mentorService from '../services/mentorService';
import studentService from '../services/studentService';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('bookings');
  const [statusFilter, setStatusFilter] = useState('all');
  const [profileId, setProfileId] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, [user]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      if (user?.accountType === 'student') {
        try {
          const studentProfile = await studentService.getMyProfile();
          setProfileId(studentProfile.studentId);
          
          const bookingsData = await bookingService.getBookingsByStudent(studentProfile.studentId);
          setBookings(bookingsData);
        } catch (err) {
          console.error('Student profile error:', err);
          setError('Please create your student profile first');
          setTimeout(() => navigate('/student-profile'), 2000);
        }
      } else if (user?.accountType === 'mentor') {
        try {
          const mentorProfile = await mentorService.getMyProfile();
          console.log('✅ Mentor profile loaded:', mentorProfile);
          
          setProfileId(mentorProfile.mentorId);
          
          const bookingsData = await bookingService.getBookingsByMentor(mentorProfile.mentorId);
          setBookings(bookingsData);
          
          const tasksData = await taskService.getTasksByMentor(mentorProfile.mentorId);
          setTasks(tasksData);
        } catch (err) {
          console.error('Mentor profile error:', err);
          setError('Please create your mentor profile first');
          setTimeout(() => navigate('/mentor-profile'), 2000);
        }
      }
    } catch (err) {
      console.error('Dashboard error:', err);
      setError(err.message || 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleAcceptBooking = async (bookingId) => {
    try {
      await bookingService.acceptBooking(bookingId, profileId);
      await fetchDashboardData();
    } catch (err) {
      setError(err.message || 'Failed to accept booking');
    }
  };

  const handleDeclineBooking = async (bookingId) => {
    try {
      await bookingService.declineBooking(bookingId, profileId);
      await fetchDashboardData();
    } catch (err) {
      setError(err.message || 'Failed to decline booking');
    }
  };

  const handleCancelBooking = async (bookingId) => {
    try {
      await bookingService.cancelBooking(bookingId, profileId, user.accountType);
      await fetchDashboardData();
    } catch (err) {
      setError(err.message || 'Failed to cancel booking');
    }
  };

  const getStatusBadge = (status) => {
    const statusStyles = {
      pending: 'bg-yellow-100 text-yellow-800',
      accepted: 'bg-green-100 text-green-800',
      declined: 'bg-red-100 text-red-800',
      cancelled: 'bg-gray-100 text-gray-800',
      completed: 'bg-blue-100 text-blue-800'
    };

    const normalizedStatus = status?.toLowerCase();

    return (
      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${statusStyles[normalizedStatus] || 'bg-gray-100 text-gray-800'}`}>
        {status}
      </span>
    );
  };

  const filteredBookings = statusFilter === 'all' 
    ? bookings 
    : bookings.filter(booking => booking.status?.toLowerCase() === statusFilter.toLowerCase());

  const getStatistics = () => {
    if (user?.accountType === 'student') {
      return {
        total: bookings.length,
        pending: bookings.filter(b => b.status?.toLowerCase() === 'pending').length,
        accepted: bookings.filter(b => b.status?.toLowerCase() === 'accepted').length,
        completed: bookings.filter(b => b.status?.toLowerCase() === 'completed').length
      };
    } else {
      return {
        total: bookings.length,
        pending: bookings.filter(b => b.status?.toLowerCase() === 'pending').length,
        accepted: bookings.filter(b => b.status?.toLowerCase() === 'accepted').length,
        tasks: tasks.length
      };
    }
  };

  const stats = getStatistics();

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-2 text-gray-600">
          Welcome back, {user?.email}
        </p>
      </div>

      {/* Error Message */}
      {error && (
        <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm font-medium text-gray-600">Total Bookings</div>
          <div className="mt-2 text-3xl font-bold text-gray-900">{stats.total}</div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm font-medium text-gray-600">Pending</div>
          <div className="mt-2 text-3xl font-bold text-yellow-600">{stats.pending}</div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm font-medium text-gray-600">Accepted</div>
          <div className="mt-2 text-3xl font-bold text-green-600">{stats.accepted}</div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm font-medium text-gray-600">
            {user?.accountType === 'mentor' ? 'My Tasks' : 'Completed'}
          </div>
          <div className="mt-2 text-3xl font-bold text-blue-600">
            {user?.accountType === 'mentor' ? stats.tasks : stats.completed}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow">
        <div className="border-b border-gray-200">
          <nav className="flex -mb-px">
            <button
              onClick={() => setActiveTab('bookings')}
              className={`px-6 py-4 text-sm font-medium border-b-2 ${
                activeTab === 'bookings'
                  ? 'border-indigo-600 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Bookings ({bookings.length})
            </button>
            {user?.accountType === 'mentor' && (
              <button
                onClick={() => setActiveTab('tasks')}
                className={`px-6 py-4 text-sm font-medium border-b-2 ${
                  activeTab === 'tasks'
                    ? 'border-indigo-600 text-indigo-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                My Tasks ({tasks.length})
              </button>
            )}
          </nav>
        </div>

        {/* Bookings Tab */}
        {activeTab === 'bookings' && (
          <div className="p-6">
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Filter by Status
              </label>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="block w-full md:w-64 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value="all">All Statuses</option>
                <option value="pending">Pending</option>
                <option value="accepted">Accepted</option>
                <option value="declined">Declined</option>
                <option value="cancelled">Cancelled</option>
                <option value="completed">Completed</option>
              </select>
            </div>

            {filteredBookings.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">No bookings found</p>
                {user?.accountType === 'student' && (
                  <button
                    onClick={() => navigate('/search')}
                    className="mt-4 inline-block bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded-md"
                  >
                    Browse Mentors
                  </button>
                )}
              </div>
            ) : (
              <div className="space-y-4">
                {filteredBookings.map((booking) => (
                  <div
                    key={booking.bookingId}
                    className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition"
                  >
                    <div className="flex justify-between items-start mb-4">
                      <div className="flex-1">
                        <h3 className="text-lg font-semibold text-gray-900">
                          {booking.taskTitle || 'Task'}
                        </h3>
                        <p className="text-sm text-gray-600 mt-1">
                          {user?.accountType === 'student' 
                            ? `Mentor: ${booking.mentorName || 'N/A'}`
                            : `Student: ${booking.studentName || 'N/A'}`
                          }
                        </p>
                        <p className="text-sm text-gray-500 mt-1">
                          Scheduled: {new Date(booking.proposedDatetime).toLocaleString()}
                        </p>
                        <p className="text-sm text-gray-500 mt-1">
                          Duration: {booking.taskDurationMinutes} minutes
                        </p>
                      </div>
                      {getStatusBadge(booking.status)}
                    </div>

                    <div className="flex gap-3 mt-4">
                      {user?.accountType === 'mentor' && booking.status?.toLowerCase() === 'pending' && (
                        <>
                          <button
                            onClick={() => handleAcceptBooking(booking.bookingId)}
                            className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-md text-sm font-medium transition"
                          >
                            Accept
                          </button>
                          <button
                            onClick={() => handleDeclineBooking(booking.bookingId)}
                            className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-md text-sm font-medium transition"
                          >
                            Decline
                          </button>
                        </>
                      )}
                      {(booking.status?.toLowerCase() === 'pending' || booking.status?.toLowerCase() === 'accepted') && (
                        <button
                          onClick={() => handleCancelBooking(booking.bookingId)}
                          className="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md text-sm font-medium transition"
                        >
                          Cancel
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Tasks Tab (Mentor Only) */}
        {activeTab === 'tasks' && user?.accountType === 'mentor' && (
          <div className="p-6">
            {tasks.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">No tasks created yet</p>
                <button
                  onClick={() => navigate('/task-creation')}
                  className="mt-4 inline-block bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded-md"
                >
                  Create Task
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {tasks.map((task) => (
                  <div
                    key={task.taskId}
                    className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition cursor-pointer"
                    onClick={() => navigate('/task-creation')}
                  >
                    {task.imageUrl && (
                      <img 
                        src={`http://localhost:8080${task.imageUrl}`} 
                        alt={task.title}
                        className="w-full h-48 object-cover rounded-md mb-4"
                      />
                    )}
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      {task.title}
                    </h3>
                    <p className="text-sm text-gray-600 mb-3 line-clamp-2">
                      {task.description}
                    </p>
                    <div className="space-y-2 text-sm text-gray-700">
                      <div>
                        <span className="font-medium">Category:</span> {task.category}
                      </div>
                      <div>
                        <span className="font-medium">Duration:</span> {task.durationMinutes} min
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;