import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import bookingService from '../services/bookingService';
import taskService from '../services/taskService';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('bookings');
  const [statusFilter, setStatusFilter] = useState('all');

  useEffect(() => {
    fetchDashboardData();
  }, [user]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      if (user?.role === 'student') {
        const bookingsData = await bookingService.getBookingsByStudent(user.userId);
        setBookings(bookingsData);
      } else if (user?.role === 'mentor') {
        const bookingsData = await bookingService.getBookingsByMentor(user.userId);
        setBookings(bookingsData);
        
        const tasksData = await taskService.getTasksByMentor(user.userId);
        setTasks(tasksData);
      }
    } catch (err) {
      setError(err.message || 'Failed to load dashboard data');
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAcceptBooking = async (bookingId) => {
    try {
      await bookingService.acceptBooking(bookingId, user.userId);
      fetchDashboardData();
    } catch (err) {
      setError(err.message || 'Failed to accept booking');
    }
  };

  const handleDeclineBooking = async (bookingId) => {
    try {
      await bookingService.declineBooking(bookingId, user.userId);
      fetchDashboardData();
    } catch (err) {
      setError(err.message || 'Failed to decline booking');
    }
  };

  const handleCancelBooking = async (bookingId) => {
    try {
      await bookingService.cancelBooking(bookingId, user.userId, user.role);
      fetchDashboardData();
    } catch (err) {
      setError(err.message || 'Failed to cancel booking');
    }
  };

  const getStatusBadge = (status) => {
    const statusStyles = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      ACCEPTED: 'bg-green-100 text-green-800',
      DECLINED: 'bg-red-100 text-red-800',
      CANCELLED: 'bg-gray-100 text-gray-800',
      COMPLETED: 'bg-blue-100 text-blue-800'
    };

    return (
      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${statusStyles[status] || 'bg-gray-100 text-gray-800'}`}>
        {status}
      </span>
    );
  };

  const filteredBookings = statusFilter === 'all' 
    ? bookings 
    : bookings.filter(booking => booking.status === statusFilter);

  const getStatistics = () => {
    if (user?.role === 'student') {
      return {
        total: bookings.length,
        pending: bookings.filter(b => b.status === 'PENDING').length,
        accepted: bookings.filter(b => b.status === 'ACCEPTED').length,
        completed: bookings.filter(b => b.status === 'COMPLETED').length
      };
    } else {
      return {
        total: bookings.length,
        pending: bookings.filter(b => b.status === 'PENDING').length,
        accepted: bookings.filter(b => b.status === 'ACCEPTED').length,
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
            {user?.role === 'mentor' ? 'My Tasks' : 'Completed'}
          </div>
          <div className="mt-2 text-3xl font-bold text-blue-600">
            {user?.role === 'mentor' ? stats.tasks : stats.completed}
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
            {user?.role === 'mentor' && (
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
            {/* Status Filter */}
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
                <option value="PENDING">Pending</option>
                <option value="ACCEPTED">Accepted</option>
                <option value="DECLINED">Declined</option>
                <option value="CANCELLED">Cancelled</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>

            {/* Bookings List */}
            {filteredBookings.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">No bookings found</p>
                {user?.role === 'student' && (
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
                          {booking.task?.title || 'Task'}
                        </h3>
                        <p className="text-sm text-gray-600 mt-1">
                          {user?.role === 'student' 
                            ? `Mentor: ${booking.mentor?.name || 'N/A'}`
                            : `Student: ${booking.student?.name || 'N/A'}`
                          }
                        </p>
                        <p className="text-sm text-gray-500 mt-1">
                          Scheduled: {new Date(booking.scheduledTime).toLocaleString()}
                        </p>
                      </div>
                      {getStatusBadge(booking.status)}
                    </div>

                    {/* Actions */}
                    <div className="flex gap-3 mt-4">
                      {user?.role === 'mentor' && booking.status === 'PENDING' && (
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
                      {(booking.status === 'PENDING' || booking.status === 'ACCEPTED') && (
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
        {activeTab === 'tasks' && user?.role === 'mentor' && (
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
                    className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition"
                  >
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
                      {task.imageUrls && task.imageUrls.length > 0 && (
                        <div>
                          <span className="font-medium">Images:</span> {task.imageUrls.length}
                        </div>
                      )}
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
