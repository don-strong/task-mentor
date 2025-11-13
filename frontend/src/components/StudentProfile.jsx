import { useState } from 'react';

function StudentProfile() {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    university: 'State University',
    major: 'Computer Science',
    graduationYear: '2025',
    bio: 'Passionate student looking to learn from industry professionals.',
    interests: ['Web Development', 'Machine Learning', 'Career Advice'],
    linkedIn: 'linkedin.com/in/johndoe',
    github: 'github.com/johndoe',
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleInterestChange = (index, value) => {
    const newInterests = [...formData.interests];
    newInterests[index] = value;
    setFormData((prev) => ({
      ...prev,
      interests: newInterests,
    }));
  };

  const addInterest = () => {
    setFormData((prev) => ({
      ...prev,
      interests: [...prev.interests, ''],
    }));
  };

  const removeInterest = (index) => {
    const newInterests = formData.interests.filter((_, i) => i !== index);
    setFormData((prev) => ({
      ...prev,
      interests: newInterests,
    }));
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.firstName) {
      newErrors.firstName = 'First name is required';
    }

    if (!formData.lastName) {
      newErrors.lastName = 'Last name is required';
    }

    if (!formData.university) {
      newErrors.university = 'University is required';
    }

    if (!formData.major) {
      newErrors.major = 'Major is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      // TODO: Replace with actual API call
      console.log('Updated profile data:', formData);
      alert('Profile updated successfully!');
      setIsEditing(false);
    } catch (error) {
      console.error('Profile update error:', error);
      alert('Failed to update profile. Please try again.');
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setErrors({});
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white shadow rounded-lg">
          {/* Header */}
          <div className="px-6 py-5 border-b border-gray-200">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold text-gray-900">Student Profile</h2>
              {!isEditing && (
                <button
                  onClick={() => setIsEditing(true)}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  Edit Profile
                </button>
              )}
            </div>
          </div>

          {/* Profile Content */}
          <form onSubmit={handleSubmit} className="px-6 py-6 space-y-6">
            {/* Personal Information */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Personal Information</h3>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="firstName" className="block text-sm font-medium text-gray-700">
                    First Name
                  </label>
                  <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    value={formData.firstName}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                  {errors.firstName && (
                    <p className="mt-1 text-sm text-red-600">{errors.firstName}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="lastName" className="block text-sm font-medium text-gray-700">
                    Last Name
                  </label>
                  <input
                    id="lastName"
                    name="lastName"
                    type="text"
                    value={formData.lastName}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                  {errors.lastName && <p className="mt-1 text-sm text-red-600">{errors.lastName}</p>}
                </div>

                <div className="sm:col-span-2">
                  <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                    Email
                  </label>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    value={formData.email}
                    disabled={true}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-gray-100 cursor-not-allowed"
                  />
                </div>
              </div>
            </div>

            {/* Academic Information */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Academic Information</h3>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="university" className="block text-sm font-medium text-gray-700">
                    University
                  </label>
                  <input
                    id="university"
                    name="university"
                    type="text"
                    value={formData.university}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                  {errors.university && (
                    <p className="mt-1 text-sm text-red-600">{errors.university}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="major" className="block text-sm font-medium text-gray-700">
                    Major
                  </label>
                  <input
                    id="major"
                    name="major"
                    type="text"
                    value={formData.major}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                  {errors.major && <p className="mt-1 text-sm text-red-600">{errors.major}</p>}
                </div>

                <div>
                  <label htmlFor="graduationYear" className="block text-sm font-medium text-gray-700">
                    Expected Graduation Year
                  </label>
                  <input
                    id="graduationYear"
                    name="graduationYear"
                    type="text"
                    value={formData.graduationYear}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                </div>
              </div>
            </div>

            {/* Bio */}
            <div>
              <label htmlFor="bio" className="block text-sm font-medium text-gray-700">
                Bio
              </label>
              <textarea
                id="bio"
                name="bio"
                rows={4}
                value={formData.bio}
                onChange={handleChange}
                disabled={!isEditing}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
              />
            </div>

            {/* Interests */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Areas of Interest
              </label>
              <div className="space-y-2">
                {formData.interests.map((interest, index) => (
                  <div key={index} className="flex gap-2">
                    <input
                      type="text"
                      value={interest}
                      onChange={(e) => handleInterestChange(index, e.target.value)}
                      disabled={!isEditing}
                      className="flex-1 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    />
                    {isEditing && (
                      <button
                        type="button"
                        onClick={() => removeInterest(index)}
                        className="px-3 py-2 bg-red-100 text-red-700 rounded-md hover:bg-red-200"
                      >
                        Remove
                      </button>
                    )}
                  </div>
                ))}
              </div>
              {isEditing && (
                <button
                  type="button"
                  onClick={addInterest}
                  className="mt-2 px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200"
                >
                  + Add Interest
                </button>
              )}
            </div>

            {/* Social Links */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Social Links</h3>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="linkedIn" className="block text-sm font-medium text-gray-700">
                    LinkedIn
                  </label>
                  <input
                    id="linkedIn"
                    name="linkedIn"
                    type="text"
                    value={formData.linkedIn}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                </div>

                <div>
                  <label htmlFor="github" className="block text-sm font-medium text-gray-700">
                    GitHub
                  </label>
                  <input
                    id="github"
                    name="github"
                    type="text"
                    value={formData.github}
                    onChange={handleChange}
                    disabled={!isEditing}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                  />
                </div>
              </div>
            </div>

            {/* Action Buttons */}
            {isEditing && (
              <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
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
                  Save Changes
                </button>
              </div>
            )}
          </form>
        </div>
      </div>
    </div>
  );
}

export default StudentProfile;
