import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import studentService from '../services/studentService';

function StudentProfile() {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [hasProfile, setHasProfile] = useState(false);
  
  const [formData, setFormData] = useState({
    name: '',
    bio: '',
    major: '',
    graduationYear: '',
    careerInterests: '',
    profilePhotoUrl: '',
  });

  const [errors, setErrors] = useState({});

  // Load profile on component mount
  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setIsLoading(true);
    try {
      const profile = await studentService.getMyProfile();
      setFormData({
        name: profile.name || '',
        bio: profile.bio || '',
        major: profile.major || '',
        graduationYear: profile.graduationYear?.toString() || '',
        careerInterests: profile.careerInterests || '',
        profilePhotoUrl: profile.profilePhotoUrl || '',
      });
      setHasProfile(true);
    } catch (error) {
      console.error('Error loading profile:', error);
      // Profile doesn't exist yet - user needs to create one
      setHasProfile(false);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({...prev, [name]: ''}));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name || formData.name.trim().length < 2) {
      newErrors.name = 'Name must be at least 2 characters';
    }

    if (!formData.major || formData.major.trim().length === 0) {
      newErrors.major = 'Major is required';
    }

    if (!formData.graduationYear) {
      newErrors.graduationYear = 'Graduation year is required';
    } else {
      const year = parseInt(formData.graduationYear);
      const currentYear = new Date().getFullYear();
      if (year < currentYear - 10 || year > currentYear + 10) {
        newErrors.graduationYear = 'Graduation year must be within 10 years';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      const profileData = {
        name: formData.name.trim(),
        bio: formData.bio?.trim() || null,
        major: formData.major.trim(),
        graduationYear: parseInt(formData.graduationYear),
        careerInterests: formData.careerInterests?.trim() || null,
        profilePhotoUrl: formData.profilePhotoUrl?.trim() || null,
      };

      if (hasProfile) {
        // Update existing profile
        await studentService.updateProfile(profileData);
        alert('Profile updated successfully!');
      } else {
        // Create new profile
        await studentService.createProfile(profileData);
        alert('Profile created successfully!');
        setHasProfile(true);
      }
      
      setIsEditing(false);
      await loadProfile(); // Reload to get updated data
    } catch (error) {
      console.error('Profile save error:', error);
      const errorMessage = error?.error || error?.message || 'Failed to save profile. Please try again.';
      setErrors({ submit: errorMessage });
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setErrors({});
    loadProfile(); // Reload original data
  };

  if (isLoading && !formData.name) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white shadow rounded-lg">
          {/* Header */}
          <div className="px-6 py-5 border-b border-gray-200">
            <div className="flex justify-between items-center">
              <div>
                <h2 className="text-2xl font-bold text-gray-900">Student Profile</h2>
                <p className="text-sm text-gray-500 mt-1">{user?.email}</p>
              </div>
              {!isEditing && hasProfile && (
                <button
                  onClick={() => setIsEditing(true)}
                  disabled={isLoading}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
                >
                  Edit Profile
                </button>
              )}
              {!hasProfile && !isEditing && (
                <button
                  onClick={() => setIsEditing(true)}
                  disabled={isLoading}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
                >
                  Create Profile
                </button>
              )}
            </div>
          </div>

          {/* Profile Content */}
          <form onSubmit={handleSubmit} className="px-6 py-6 space-y-6">
            {errors.submit && (
              <div className="rounded-md bg-red-50 p-4">
                <p className="text-sm text-red-800">{errors.submit}</p>
              </div>
            )}

            {/* Personal Information */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Personal Information</h3>
              <div className="grid grid-cols-1 gap-4">
                <div>
                  <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                    Full Name *
                  </label>
                  <input
                    id="name"
                    name="name"
                    type="text"
                    value={formData.name}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="John Doe"
                  />
                  {errors.name && (
                    <p className="mt-1 text-sm text-red-600">{errors.name}</p>
                  )}
                </div>

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
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="Tell mentors about yourself..."
                  />
                </div>
              </div>
            </div>

            {/* Academic Information */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Academic Information</h3>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="major" className="block text-sm font-medium text-gray-700">
                    Major *
                  </label>
                  <input
                    id="major"
                    name="major"
                    type="text"
                    value={formData.major}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="Computer Science"
                  />
                  {errors.major && <p className="mt-1 text-sm text-red-600">{errors.major}</p>}
                </div>

                <div>
                  <label htmlFor="graduationYear" className="block text-sm font-medium text-gray-700">
                    Expected Graduation Year *
                  </label>
                  <input
                    id="graduationYear"
                    name="graduationYear"
                    type="number"
                    value={formData.graduationYear}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="2025"
                  />
                  {errors.graduationYear && (
                    <p className="mt-1 text-sm text-red-600">{errors.graduationYear}</p>
                  )}
                </div>
              </div>
            </div>

            {/* Career Interests */}
            <div>
              <label htmlFor="careerInterests" className="block text-sm font-medium text-gray-700">
                Career Interests
              </label>
              <textarea
                id="careerInterests"
                name="careerInterests"
                rows={3}
                value={formData.careerInterests}
                onChange={handleChange}
                disabled={!isEditing || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                placeholder="Web Development, Machine Learning, Career Advice"
              />
              <p className="mt-1 text-sm text-gray-500">
                Separate multiple interests with commas
              </p>
            </div>

            {/* Profile Photo URL */}
            <div>
              <label htmlFor="profilePhotoUrl" className="block text-sm font-medium text-gray-700">
                Profile Photo URL
              </label>
              <input
                id="profilePhotoUrl"
                name="profilePhotoUrl"
                type="url"
                value={formData.profilePhotoUrl}
                onChange={handleChange}
                disabled={!isEditing || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                placeholder="https://example.com/photo.jpg"
              />
            </div>

            {/* Action Buttons */}
            {isEditing && (
              <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
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
                  {isLoading ? 'Saving...' : hasProfile ? 'Save Changes' : 'Create Profile'}
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