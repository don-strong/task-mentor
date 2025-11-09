import { useState } from 'react';

function TaskCreation() {
  const [tasks, setTasks] = useState([
    {
      id: 1,
      title: 'Resume Review',
      description: 'I will review your resume and provide detailed feedback',
      category: 'Career Advice',
      duration: '30',
      price: '25',
    },
    {
      id: 2,
      title: 'Mock Interview',
      description: 'Practice technical or behavioral interviews',
      category: 'Interview Prep',
      duration: '60',
      price: '50',
    },
  ]);

  const [isCreating, setIsCreating] = useState(false);
  const [newTask, setNewTask] = useState({
    title: '',
    description: '',
    category: '',
    duration: '',
    price: '',
  });

  const [errors, setErrors] = useState({});

  const categories = [
    'Resume Review',
    'Interview Prep',
    'Career Advice',
    'Code Review',
    'Portfolio Review',
    'Technical Skills',
    'Networking',
    'Other',
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewTask((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validateTask = () => {
    const newErrors = {};

    if (!newTask.title.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!newTask.description.trim()) {
      newErrors.description = 'Description is required';
    }

    if (!newTask.category) {
      newErrors.category = 'Category is required';
    }

    if (!newTask.duration || newTask.duration <= 0) {
      newErrors.duration = 'Duration must be greater than 0';
    }

    if (!newTask.price || newTask.price < 0) {
      newErrors.price = 'Price must be 0 or greater';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateTask()) {
      return;
    }

    try {
      // TODO: Replace with actual API call
      const newTaskWithId = {
        ...newTask,
        id: tasks.length + 1,
      };
      setTasks([...tasks, newTaskWithId]);
      setNewTask({
        title: '',
        description: '',
        category: '',
        duration: '',
        price: '',
      });
      setIsCreating(false);
      setErrors({});
      alert('Task created successfully!');
    } catch (error) {
      console.error('Task creation error:', error);
      alert('Failed to create task. Please try again.');
    }
  };

  const handleDelete = (taskId) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      setTasks(tasks.filter((task) => task.id !== taskId));
      alert('Task deleted successfully!');
    }
  };

  const handleCancel = () => {
    setIsCreating(false);
    setNewTask({
      title: '',
      description: '',
      category: '',
      duration: '',
      price: '',
    });
    setErrors({});
  };

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

        {/* Create Task Button */}
        {!isCreating && (
          <div className="mb-6">
            <button
              onClick={() => setIsCreating(true)}
              className="px-6 py-3 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
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
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
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
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Describe what you'll provide in this session..."
                />
                {errors.description && (
                  <p className="mt-1 text-sm text-red-600">{errors.description}</p>
                )}
              </div>

              <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                <div>
                  <label htmlFor="category" className="block text-sm font-medium text-gray-700">
                    Category *
                  </label>
                  <select
                    id="category"
                    name="category"
                    value={newTask.category}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
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
                  <label htmlFor="duration" className="block text-sm font-medium text-gray-700">
                    Duration (minutes) *
                  </label>
                  <input
                    id="duration"
                    name="duration"
                    type="number"
                    value={newTask.duration}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                    placeholder="30"
                  />
                  {errors.duration && <p className="mt-1 text-sm text-red-600">{errors.duration}</p>}
                </div>

                <div>
                  <label htmlFor="price" className="block text-sm font-medium text-gray-700">
                    Price ($) *
                  </label>
                  <input
                    id="price"
                    name="price"
                    type="number"
                    value={newTask.price}
                    onChange={handleChange}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                    placeholder="25"
                  />
                  {errors.price && <p className="mt-1 text-sm text-red-600">{errors.price}</p>}
                </div>
              </div>

              <div className="flex justify-end gap-4 pt-4">
                <button
                  type="button"
                  onClick={handleCancel}
                  className="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  Create Task
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
              <div key={task.id} className="bg-white shadow rounded-lg p-6">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900">{task.title}</h3>
                    <p className="mt-2 text-gray-600">{task.description}</p>
                    <div className="mt-4 flex flex-wrap gap-4 text-sm">
                      <span className="inline-flex items-center px-3 py-1 rounded-full bg-indigo-100 text-indigo-800">
                        {task.category}
                      </span>
                      <span className="text-gray-600">⏱️ {task.duration} minutes</span>
                      <span className="text-gray-600">💵 ${task.price}</span>
                    </div>
                  </div>
                  <button
                    onClick={() => handleDelete(task.id)}
                    className="ml-4 px-4 py-2 bg-red-100 text-red-700 rounded-md hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-red-500"
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
