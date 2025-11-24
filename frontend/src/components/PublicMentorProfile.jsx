import { useState } from 'react';

function PublicMentorProfile() {
  // Mock mentor data - would come from API in real implementation
  const [mentor] = useState({
    id: 1,
    firstName: 'Jane',
    lastName: 'Smith',
    title: 'Senior Software Engineer',
    company: 'Tech Corp',
    yearsOfExperience: 8,
    bio: 'Experienced software engineer passionate about mentoring the next generation of developers. I have worked on large-scale web applications and enjoy sharing my knowledge with aspiring developers.',
    expertise: ['Web Development', 'System Design', 'Career Guidance'],
    linkedIn: 'linkedin.com/in/janesmith',
    website: 'janesmith.dev',
    availability: 'weekends',
    profileImage: null,
  });

  const [tasks] = useState([
    {
      id: 1,
      title: 'Resume Review',
      description: 'I will review your resume and provide detailed feedback on content, structure, and formatting',
      category: 'Career Advice',
      duration: 30,
      price: 25,
    },
    {
      id: 2,
      title: 'Mock Interview',
      description: 'Practice technical or behavioral interviews with real-time feedback',
      category: 'Interview Prep',
      duration: 60,
      price: 50,
    },
    {
      id: 3,
      title: 'Code Review Session',
      description: 'Get feedback on your code quality, architecture, and best practices',
      category: 'Code Review',
      duration: 45,
      price: 40,
    },
  ]);

  const [selectedTask, setSelectedTask] = useState(null);

  const handleBookTask = (task) => {
    setSelectedTask(task);
    // TODO: Implement booking modal or navigate to booking page
    alert(`Booking ${task.title}. This would open a booking modal in the full implementation.`);
  };

  const availabilityDisplay = {
    weekdays: 'Weekdays',
    weekends: 'Weekends',
    both: 'Weekdays & Weekends',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header Section */}
      <div className="bg-white shadow">
        <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col sm:flex-row gap-6">
            {/* Profile Image Placeholder */}
            <div className="shrink-0">
              <div className="h-32 w-32 rounded-full bg-indigo-200 flex items-center justify-center text-4xl text-indigo-700 font-bold">
                {mentor.firstName[0]}
                {mentor.lastName[0]}
              </div>
            </div>

            {/* Mentor Info */}
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900">
                {mentor.firstName} {mentor.lastName}
              </h1>
              <p className="text-xl text-gray-600 mt-1">
                {mentor.title} at {mentor.company}
              </p>
              <p className="text-gray-600 mt-1">{mentor.yearsOfExperience} years of experience</p>

              {/* Expertise Tags */}
              <div className="mt-4 flex flex-wrap gap-2">
                {mentor.expertise.map((area, index) => (
                  <span
                    key={index}
                    className="inline-flex items-center px-3 py-1 rounded-full text-sm bg-indigo-100 text-indigo-800"
                  >
                    {area}
                  </span>
                ))}
              </div>

              {/* Availability */}
              <div className="mt-4">
                <span className="text-sm text-gray-600">
                  📅 Available: {availabilityDisplay[mentor.availability]}
                </span>
              </div>
            </div>
          </div>

          {/* Bio */}
          <div className="mt-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-2">About</h2>
            <p className="text-gray-700">{mentor.bio}</p>
          </div>

          {/* Social Links */}
          {(mentor.linkedIn || mentor.website) && (
            <div className="mt-6 flex gap-4">
              {mentor.linkedIn && (
                <a
                  href={`https://${mentor.linkedIn}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-indigo-600 hover:text-indigo-800 flex items-center gap-1"
                >
                  🔗 LinkedIn
                </a>
              )}
              {mentor.website && (
                <a
                  href={`https://${mentor.website}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-indigo-600 hover:text-indigo-800 flex items-center gap-1"
                >
                  🌐 Website
                </a>
              )}
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
              <div key={task.id} className="bg-white shadow rounded-lg p-6 hover:shadow-lg transition-shadow">
                <div className="flex justify-between items-start mb-3">
                  <h3 className="text-xl font-semibold text-gray-900">{task.title}</h3>
                  <span className="text-2xl font-bold text-indigo-600">${task.price}</span>
                </div>

                <p className="text-gray-600 mb-4">{task.description}</p>

                <div className="flex items-center gap-4 text-sm text-gray-600 mb-4">
                  <span className="inline-flex items-center px-3 py-1 rounded-full bg-gray-100 text-gray-800">
                    {task.category}
                  </span>
                  <span>⏱️ {task.duration} min</span>
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

      {/* Reviews Section (Placeholder) */}
      <div className="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Reviews</h2>
        <div className="bg-white shadow rounded-lg p-6">
          <p className="text-gray-500 text-center">No reviews yet</p>
        </div>
      </div>
    </div>
  );
}

export default PublicMentorProfile;
