import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import taskService from '../services/taskService';
import mentorService from '../services/mentorService';

function TaskCreation() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [mentorId, setMentorId] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  
  const [newTask, setNewTask] = useState({
    title: '',
    description: '',
    category: '',
    durationMinutes: '',
  });

  const [errors, setErrors] = useState({});

  // Backend-defined categories (matching TaskService.java)
  const categories = [
    'Resume Review',
    'Interview Prep',
    'Career Advice',
    'Technical Mentoring',
    'Networking',
    'Project Review',
    'Programming',
    'Other',
  ];

  useEffect(() => {
    loadMentorAndTasks();
  }, []);

  const loadMentorAndTasks = async () => {
    setIsLoading(true);
    try {
      // ✅ Use the new /mentors/me endpoint
      const mentorProfile = await mentorService.getMyProfile();
      console.log('✅ Mentor profile loaded:', mentorProfile);
      
      setMentorId(mentorProfile.mentorId);
      
      // Load tasks for this mentor
      const mentorTasks = await taskService.getTasksByMentor(mentorProfile.mentorId);
      setTasks(mentorTasks);
      
    } catch (error) {
      console.error('Error loading mentor profile:', error);
      setErrors({ submit: 'Please create your mentor profile first' });
      
      // Redirect to mentor profile creation after 2 seconds
      setTimeout(() => navigate('/mentor-profile'), 2000);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewTask((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (errors[name]) {
      setErrors(prev => ({...prev, [name]: ''}));
    }
  };

  const validateTask = () => {
    const newErrors = {};

    if (!newTask.title || newTask.title.trim().length < 5) {
      newErrors.title = 'Title must be at least 5 characters';
    }

    if (!newTask.description || newTask.description.trim().length < 20) {
      newErrors.description = 'Description must be at least 20 characters';
    }

    if (!newTask.category) {
      newErrors.category = 'Category is required';
    }

    const duration = parseInt(newTask.durationMinutes);
    if (!duration || duration < 15 || duration > 480) {
      newErrors.durationMinutes = 'Duration must be between 15 and 480 minutes';
    } else if (duration % 15 !== 0) {
      newErrors.durationMinutes = 'Duration must be in 15-minute increments';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateTask()) {
      return;
    }

    if (!mentorId) {
      setErrors({ submit: 'Please create a mentor profile first' });
      return;
    }

    setIsLoading(true);

    try {
      const taskData = {
        mentorId: mentorId,
        title: newTask.title.trim(),
        description: newTask.description.trim(),
        durationMinutes: parseInt(newTask.durationMinutes),
        category: newTask.category,
      };

      await taskService.createTask(taskData);
      
      // Reload tasks
      await loadMentorAndTasks();
      
      // Reset form
      setNewTask({
        title: '',
        description: '',
        category: '',
        durationMinutes: '',
      });
      setIsCreating(false);
      setErrors({});
      alert('Task created successfully!');
    } catch (error) {
      console.error('Task creation error:', error);
      const errorMessage = error?.error || error?.message || 'Failed to create task';
      setErrors({ submit: errorMessage });
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (taskId) => {
    if (!window.confirm('Are you sure you want to delete this task?')) {
      return;
    }

    setIsLoading(true);
    try {
      await taskService.deleteTask(taskId, mentorId);
      await loadMentorAndTasks(); // Reload tasks
      alert('Task deleted successfully!');
    } catch (error) {
      console.error('Task deletion error:', error);
      alert(error?.error || 'Failed to delete task');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    setIsCreating(false);
    setNewTask({
      title: '',
      description: '',
      category: '',
      durationMinutes: '',
    });
    setErrors({});
  };

  if (isLoading && tasks.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading tasks...</p>
        </div>
      </div>
    );
  }

  if (!mentorId && !isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 text-lg mb-4">Please create a mentor profile first</p>
          <p className="text-gray-600">Redirecting...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Task Menu Management</h1>
          <p className="mt-2 text-gray-600">
            Create and manage the tasks you offer to students
          </p>
        </div>

        {/* Error Message */}
        {errors.submit && (
          <div className="mb-6 rounded-md bg-red-50 p-4">
            <p className="text-sm text-red-800">{errors.submit}</p>
          </div>
        )}

        {/* Create Task Button */}
        {!isCreating && (
          <div className="mb-6">
            <button
              onClick={() => setIsCreating(true)}
              disabled={isLoading}
              className="px-6 py-3 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              + Create New Task
            </button>
          </div>
        )}

        {/* Create Task Form */}
        {isCreating && (
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Create New Task</h2>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                  Task Title *
                </label>
                <input
                  id="title"
                  name="title"
                  type="text"
                  value={newTask.title}
                  onChange={handleChange}
                  disabled={isLoading}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:opacity-50"
                  placeholder="e.g., Resume Review"
                />
                {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title}</p>}
              </div>

              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                  Description *
                </label>
                <textarea
                  id="description"
                  name="description"
                  rows={3}
                  value={newTask.description}
                  onChange={handleChange}
                  disabled={isLoading}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:opacity-50"
                  placeholder="Describe what you'll provide in this session..."
                />
                {errors.description && (
                  <p className="mt-1 text-sm text-red-600">{errors.description}</p>
                )}
              </div>

              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="category" className="block text-sm font-medium text-gray-700">
                    Category *
                  </label>
                  <select
                    id="category"
                    name="category"
                    value={newTask.category}
                    onChange={handleChange}
                    disabled={isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:opacity-50"
                  >
                    <option value="">Select category</option>
                    {categories.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                  {errors.category && (
                    <p className="mt-1 text-sm text-red-600">{errors.category}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="durationMinutes" className="block text-sm font-medium text-gray-700">
                    Duration (minutes) *
                  </label>
                  <input
                    id="durationMinutes"
                    name="durationMinutes"
                    type="number"
                    value={newTask.durationMinutes}
                    onChange={handleChange}
                    disabled={isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:opacity-50"
                    placeholder="30"
                    step="15"
                  />
                  {errors.durationMinutes && (
                    <p className="mt-1 text-sm text-red-600">{errors.durationMinutes}</p>
                  )}
                  <p className="mt-1 text-sm text-gray-500">
                    Must be between 15-480 minutes in 15-minute increments
                  </p>
                </div>
              </div>

              <div className="flex justify-end gap-4 pt-4">
                <button
                  type="button"
                  onClick={handleCancel}
                  disabled={isLoading}
                  className="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 disabled:opacity-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
                >
                  {isLoading ? 'Creating...' : 'Create Task'}
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Existing Tasks List */}
        <div className="space-y-4">
          <h2 className="text-xl font-semibold text-gray-900">Your Tasks</h2>
          {tasks.length === 0 ? (
            <div className="bg-white shadow rounded-lg p-6 text-center text-gray-500">
              No tasks yet. Create your first task to get started!
            </div>
          ) : (
            tasks.map((task) => (
              <div key={task.taskId} className="bg-white shadow rounded-lg p-6">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900">{task.title}</h3>
                    <p className="mt-2 text-gray-600">{task.description}</p>
                    <div className="mt-4 flex flex-wrap gap-4 text-sm">
                      <span className="inline-flex items-center px-3 py-1 rounded-full bg-indigo-100 text-indigo-800">
                        {task.category}
                      </span>
                      <span className="text-gray-600">⏱️ {task.durationMinutes} minutes</span>
                    </div>
                  </div>
                  <button
                    onClick={() => handleDelete(task.taskId)}
                    disabled={isLoading}
                    className="ml-4 px-4 py-2 bg-red-100 text-red-700 rounded-md hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}

export default TaskCreation;