import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import searchService from '../services/searchService';

const Search = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  
  // Search type and results
  const [searchType, setSearchType] = useState('mentors');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Filter options from API
  const [filterOptions, setFilterOptions] = useState({
    categories: [],
    companies: [],
    majors: []
  });

  // Mentor filters
  const [mentorFilters, setMentorFilters] = useState({
    name: '',
    company: '',
    industry: '',
    expertise: '',
    minYearsExperience: ''
  });

  // Task filters
  const [taskFilters, setTaskFilters] = useState({
    title: '',
    category: '',
    mentorId: '',
    minDuration: '',
    maxDuration: ''
  });

  // Student filters
  const [studentFilters, setStudentFilters] = useState({
    name: '',
    major: '',
    graduationYear: '',
    minGraduationYear: '',
    careerInterests: ''
  });

  // Combined mentor and task search filters
  const [combinedFilters, setCombinedFilters] = useState({
    mentorName: '',
    expertise: '',
    taskCategory: '',
    maxDuration: ''
  });

  useEffect(() => {
    loadFilterOptions();
  }, []);

  useEffect(() => {
    handleSearch();
  }, [searchType]);

  const loadFilterOptions = async () => {
    try {
      const options = await searchService.getFilterOptions();
      setFilterOptions(options);
    } catch (err) {
      console.error('Failed to load filter options:', err);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      setError(null);
      let data;

      switch (searchType) {
        case 'mentors':
          data = await searchService.searchMentors(mentorFilters);
          setResults(data.mentors || []);
          break;
        case 'tasks':
          data = await searchService.searchTasks(taskFilters);
          setResults(data.tasks || []);
          break;
        case 'students':
          data = await searchService.searchStudents(studentFilters);
          setResults(data.students || []);
          break;
        case 'combined':
          data = await searchService.searchMentorsWithTasks(combinedFilters);
          setResults(data.mentors || []);
          break;
        default:
          setResults([]);
      }
    } catch (err) {
      setError(err.message || 'Search failed');
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setMentorFilters({
      name: '',
      company: '',
      industry: '',
      expertise: '',
      minYearsExperience: ''
    });
    setTaskFilters({
      title: '',
      category: '',
      mentorId: '',
      minDuration: '',
      maxDuration: ''
    });
    setStudentFilters({
      name: '',
      major: '',
      graduationYear: '',
      minGraduationYear: '',
      careerInterests: ''
    });
    setCombinedFilters({
      mentorName: '',
      expertise: '',
      taskCategory: '',
      maxDuration: ''
    });
  };

  const renderMentorFilters = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
        <input
          type="text"
          value={mentorFilters.name}
          onChange={(e) => setMentorFilters({ ...mentorFilters, name: e.target.value })}
          placeholder="Search by name..."
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Company</label>
        <select
          value={mentorFilters.company}
          onChange={(e) => setMentorFilters({ ...mentorFilters, company: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Companies</option>
          {filterOptions.companies.map((company) => (
            <option key={company} value={company}>{company}</option>
          ))}
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Industry</label>
        <input
          type="text"
          value={mentorFilters.industry}
          onChange={(e) => setMentorFilters({ ...mentorFilters, industry: e.target.value })}
          placeholder="e.g., Technology"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Expertise</label>
        <input
          type="text"
          value={mentorFilters.expertise}
          onChange={(e) => setMentorFilters({ ...mentorFilters, expertise: e.target.value })}
          placeholder="e.g., Machine Learning"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Min Years Experience</label>
        <input
          type="number"
          value={mentorFilters.minYearsExperience}
          onChange={(e) => setMentorFilters({ ...mentorFilters, minYearsExperience: e.target.value })}
          placeholder="0"
          min="0"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
    </div>
  );

  const renderTaskFilters = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
        <input
          type="text"
          value={taskFilters.title}
          onChange={(e) => setTaskFilters({ ...taskFilters, title: e.target.value })}
          placeholder="Search by title..."
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
        <select
          value={taskFilters.category}
          onChange={(e) => setTaskFilters({ ...taskFilters, category: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Categories</option>
          {filterOptions.categories.map((category) => (
            <option key={category} value={category}>{category}</option>
          ))}
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Min Duration (min)</label>
        <input
          type="number"
          value={taskFilters.minDuration}
          onChange={(e) => setTaskFilters({ ...taskFilters, minDuration: e.target.value })}
          placeholder="0"
          min="0"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Max Duration (min)</label>
        <input
          type="number"
          value={taskFilters.maxDuration}
          onChange={(e) => setTaskFilters({ ...taskFilters, maxDuration: e.target.value })}
          placeholder="120"
          min="0"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
    </div>
  );

  const renderStudentFilters = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
        <input
          type="text"
          value={studentFilters.name}
          onChange={(e) => setStudentFilters({ ...studentFilters, name: e.target.value })}
          placeholder="Search by name..."
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Major</label>
        <select
          value={studentFilters.major}
          onChange={(e) => setStudentFilters({ ...studentFilters, major: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Majors</option>
          {filterOptions.majors.map((major) => (
            <option key={major} value={major}>{major}</option>
          ))}
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Graduation Year</label>
        <input
          type="number"
          value={studentFilters.graduationYear}
          onChange={(e) => setStudentFilters({ ...studentFilters, graduationYear: e.target.value })}
          placeholder="2024"
          min="1900"
          max="2100"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Min Graduation Year</label>
        <input
          type="number"
          value={studentFilters.minGraduationYear}
          onChange={(e) => setStudentFilters({ ...studentFilters, minGraduationYear: e.target.value })}
          placeholder="2020"
          min="1900"
          max="2100"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Career Interests</label>
        <input
          type="text"
          value={studentFilters.careerInterests}
          onChange={(e) => setStudentFilters({ ...studentFilters, careerInterests: e.target.value })}
          placeholder="e.g., Software Engineering"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
    </div>
  );

  const renderCombinedFilters = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Mentor Name</label>
        <input
          type="text"
          value={combinedFilters.mentorName}
          onChange={(e) => setCombinedFilters({ ...combinedFilters, mentorName: e.target.value })}
          placeholder="Search by name..."
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Expertise</label>
        <input
          type="text"
          value={combinedFilters.expertise}
          onChange={(e) => setCombinedFilters({ ...combinedFilters, expertise: e.target.value })}
          placeholder="e.g., Data Science"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Task Category</label>
        <select
          value={combinedFilters.taskCategory}
          onChange={(e) => setCombinedFilters({ ...combinedFilters, taskCategory: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Categories</option>
          {filterOptions.categories.map((category) => (
            <option key={category} value={category}>{category}</option>
          ))}
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Max Duration (min)</label>
        <input
          type="number"
          value={combinedFilters.maxDuration}
          onChange={(e) => setCombinedFilters({ ...combinedFilters, maxDuration: e.target.value })}
          placeholder="120"
          min="0"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
    </div>
  );

  const renderMentorCard = (mentor) => (
    <div
      key={mentor.mentorId}
      className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition cursor-pointer"
      onClick={() => navigate(`/mentor/${mentor.mentorId}`)}
    >
      <div className="flex items-start gap-4">
        {mentor.profilePhotoUrl ? (
          <img
            src={mentor.profilePhotoUrl}
            alt={mentor.name}
            className="w-16 h-16 rounded-full object-cover"
          />
        ) : (
          <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center">
            <span className="text-2xl font-bold text-indigo-600">
              {mentor.name?.charAt(0) || 'M'}
            </span>
          </div>
        )}
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900">{mentor.name}</h3>
          <p className="text-sm text-gray-600">{mentor.roleTitle}</p>
          <p className="text-sm text-gray-500">{mentor.company}</p>
        </div>
      </div>
      <p className="mt-4 text-sm text-gray-700 line-clamp-2">{mentor.bio}</p>
      <div className="mt-4 flex flex-wrap gap-2">
        {mentor.yearsExperience && (
          <span className="px-3 py-1 bg-blue-100 text-blue-700 text-xs rounded-full">
            {mentor.yearsExperience} years exp
          </span>
        )}
        {mentor.taskCount > 0 && (
          <span className="px-3 py-1 bg-green-100 text-green-700 text-xs rounded-full">
            {mentor.taskCount} tasks
          </span>
        )}
      </div>
      {mentor.expertiseAreas && (
        <div className="mt-3 flex flex-wrap gap-2">
          {mentor.expertiseAreas.split(',').slice(0, 3).map((expertise, idx) => (
            <span key={idx} className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded">
              {expertise.trim()}
            </span>
          ))}
        </div>
      )}
    </div>
  );

  const renderTaskCard = (task) => (
    <div
      key={task.taskId}
      className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition"
    >
      <div className="flex justify-between items-start mb-3">
        <h3 className="text-lg font-semibold text-gray-900">{task.title}</h3>
        <span className="px-3 py-1 bg-indigo-100 text-indigo-700 text-xs rounded-full">
          {task.category}
        </span>
      </div>
      <p className="text-sm text-gray-700 mb-4 line-clamp-2">{task.description}</p>
      <div className="flex items-center justify-between text-sm">
        <span className="text-gray-600">
          <span className="font-medium">Mentor:</span> {task.mentorName}
        </span>
        <span className="text-gray-600">
          <span className="font-medium">Duration:</span> {task.durationMinutes} min
        </span>
      </div>
      <button
        onClick={() => navigate(`/mentor/${task.mentorId}`)}
        className="mt-4 w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-md text-sm font-medium transition"
      >
        View Mentor Profile
      </button>
    </div>
  );

  const renderStudentCard = (student) => (
    <div
      key={student.studentId}
      className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition"
    >
      <div className="flex items-start gap-4">
        <div className="w-16 h-16 rounded-full bg-purple-100 flex items-center justify-center">
          <span className="text-2xl font-bold text-purple-600">
            {student.name?.charAt(0) || 'S'}
          </span>
        </div>
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900">{student.name}</h3>
          <p className="text-sm text-gray-600">{student.major}</p>
          <p className="text-sm text-gray-500">Class of {student.graduationYear}</p>
        </div>
      </div>
      {student.bio && (
        <p className="mt-4 text-sm text-gray-700 line-clamp-2">{student.bio}</p>
      )}
      {student.careerInterests && (
        <div className="mt-3 flex flex-wrap gap-2">
          {student.careerInterests.split(',').slice(0, 3).map((interest, idx) => (
            <span key={idx} className="px-2 py-1 bg-purple-100 text-purple-600 text-xs rounded">
              {interest.trim()}
            </span>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Search</h1>
        <p className="mt-2 text-gray-600">
          Find mentors, tasks, or students using filters below
        </p>
      </div>

      {/* Search Type Selector */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-3">Search For</label>
        <div className="flex flex-wrap gap-3">
          <button
            onClick={() => setSearchType('mentors')}
            className={`px-6 py-2 rounded-md font-medium transition ${
              searchType === 'mentors'
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Mentors
          </button>
          <button
            onClick={() => setSearchType('tasks')}
            className={`px-6 py-2 rounded-md font-medium transition ${
              searchType === 'tasks'
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Tasks
          </button>
          {user?.role === 'mentor' && (
            <button
              onClick={() => setSearchType('students')}
              className={`px-6 py-2 rounded-md font-medium transition ${
                searchType === 'students'
                  ? 'bg-indigo-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              Students
            </button>
          )}
          <button
            onClick={() => setSearchType('combined')}
            className={`px-6 py-2 rounded-md font-medium transition ${
              searchType === 'combined'
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Mentors + Tasks
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Filters</h2>
          <button
            onClick={clearFilters}
            className="text-sm text-indigo-600 hover:text-indigo-700 font-medium"
          >
            Clear All
          </button>
        </div>
        
        {searchType === 'mentors' && renderMentorFilters()}
        {searchType === 'tasks' && renderTaskFilters()}
        {searchType === 'students' && renderStudentFilters()}
        {searchType === 'combined' && renderCombinedFilters()}

        <div className="mt-6">
          <button
            onClick={handleSearch}
            disabled={loading}
            className="w-full md:w-auto px-8 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-md font-medium transition disabled:opacity-50"
          >
            {loading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Results */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-lg font-semibold text-gray-900">
            Results {results.length > 0 && `(${results.length})`}
          </h2>
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
          </div>
        ) : results.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">No results found. Try adjusting your filters.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {searchType === 'mentors' && results.map(renderMentorCard)}
            {searchType === 'tasks' && results.map(renderTaskCard)}
            {searchType === 'students' && results.map(renderStudentCard)}
            {searchType === 'combined' && results.map(renderMentorCard)}
          </div>
        )}
      </div>
    </div>
  );
};

export default Search;
