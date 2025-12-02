import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import mentorService from '../services/mentorService';

function MentorProfile() {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [hasProfile, setHasProfile] = useState(false);
  const [mentorId, setMentorId] = useState(null);
  
  const [formData, setFormData] = useState({
    name: '',
    bio: '',
    roleTitle: '',
    company: '',
    yearsExperience: '',
    industries: '',
    expertiseAreas: '',
    profilePhotoUrl: '',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    setIsLoading(true);
    try {
      // Try to get mentor profile by current user
      const mentors = await mentorService.getAllMentors();
      const myMentor = mentors.find(m => m.user?.userId === user?.userId);
      
      if (myMentor) {
        setFormData({
          name: myMentor.name || '',
          bio: myMentor.bio || '',
          roleTitle: myMentor.roleTitle || '',
          company: myMentor.company || '',
          yearsExperience: myMentor.yearsExperience?.toString() || '',
          industries: myMentor.industries || '',
          expertiseAreas: myMentor.expertiseAreas || '',
          profilePhotoUrl: myMentor.profilePhotoUrl || '',
        });
        setMentorId(myMentor.mentorId);
        setHasProfile(true);
      } else {
        setHasProfile(false);
      }
    } catch (error) {
      console.error('Error loading profile:', error);
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
    if (errors[name]) {
      setErrors(prev => ({...prev, [name]: ''}));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name || formData.name.trim().length < 2) {
      newErrors.name = 'Name must be at least 2 characters';
    }

    if (formData.yearsExperience) {
      const years = parseInt(formData.yearsExperience);
      if (isNaN(years) || years < 0 || years > 60) {
        newErrors.yearsExperience = 'Years of experience must be between 0 and 60';
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
        userId: user.userId,
        name: formData.name.trim(),
        bio: formData.bio?.trim() || null,
        roleTitle: formData.roleTitle?.trim() || null,
        company: formData.company?.trim() || null,
        yearsExperience: formData.yearsExperience ? parseInt(formData.yearsExperience) : null,
        industries: formData.industries?.trim() || null,
        expertiseAreas: formData.expertiseAreas?.trim() || null,
        profilePhotoUrl: formData.profilePhotoUrl?.trim() || null,
      };

      if (hasProfile && mentorId) {
        // Update existing profile
        await mentorService.updateProfile(mentorId, profileData);
        alert('Profile updated successfully!');
      } else {
        // Create new profile
        await mentorService.createProfile(profileData);
        alert('Profile created successfully!');
        setHasProfile(true);
      }
      
      setIsEditing(false);
      await loadProfile();
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
    loadProfile();
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
    <div className="min-h-screen bg-indigo-600 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white shadow rounded-lg">
          {/* Header */}
          <div className="px-6 py-5 border-b border-gray-200">
            <div className="flex justify-between items-center">
              <div>
                <h2 className="text-2xl font-bold text-gray-900">Mentor Profile</h2>
                <p className="text-sm text-gray-500 mt-1">{user?.email}</p>
              </div>
              {!isEditing && hasProfile && (
                <button
                  onClick={() => setIsEditing(true)}
                  disabled={isLoading}
                  style={{ backgroundColor: '#581c87' }}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
                >
                  Edit Profile
                </button>
              )}
              {!hasProfile && !isEditing && (
                <button
                  onClick={() => setIsEditing(true)}
                  disabled={isLoading}
                  className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 "
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
              <div className="space-y-4">
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
                  />
                  {errors.name && (
                    <p className="mt-1 text-sm text-red-600">{errors.name}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="bio" className="block text-sm font-medium text-gray-700">
                    Professional Bio
                  </label>
                  <textarea
                    id="bio"
                    name="bio"
                    rows={4}
                    value={formData.bio}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="Tell students about your background..."
                  />
                </div>
              </div>
            </div>

            {/* Professional Information */}
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Professional Information</h3>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="roleTitle" className="block text-sm font-medium text-gray-700">
                    Job Title
                  </label>
                  <input
                    id="roleTitle"
                    name="roleTitle"
                    type="text"
                    value={formData.roleTitle}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="Senior Software Engineer"
                  />
                </div>

                <div>
                  <label htmlFor="company" className="block text-sm font-medium text-gray-700">
                    Company
                  </label>
                  <input
                    id="company"
                    name="company"
                    type="text"
                    value={formData.company}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="Tech Corp"
                  />
                </div>

                <div>
                  <label htmlFor="yearsExperience" className="block text-sm font-medium text-gray-700">
                    Years of Experience
                  </label>
                  <input
                    id="yearsExperience"
                    name="yearsExperience"
                    type="number"
                    value={formData.yearsExperience}
                    onChange={handleChange}
                    disabled={!isEditing || isLoading}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                    placeholder="8"
                  />
                  {errors.yearsExperience && (
                    <p className="mt-1 text-sm text-red-600">{errors.yearsExperience}</p>
                  )}
                </div>
              </div>
            </div>

            {/* Industries & Expertise */}
            <div>
              <label htmlFor="industries" className="block text-sm font-medium text-gray-700">
                Industries
              </label>
              <textarea
                id="industries"
                name="industries"
                rows={2}
                value={formData.industries}
                onChange={handleChange}
                disabled={!isEditing || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                placeholder="Technology, Finance, Healthcare"
              />
              <p className="mt-1 text-sm text-gray-500">
                Separate multiple industries with commas
              </p>
            </div>

            <div>
              <label htmlFor="expertiseAreas" className="block text-sm font-medium text-gray-700">
                Areas of Expertise
              </label>
              <textarea
                id="expertiseAreas"
                name="expertiseAreas"
                rows={2}
                value={formData.expertiseAreas}
                onChange={handleChange}
                disabled={!isEditing || isLoading}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                placeholder="Web Development, System Design, Career Guidance"
              />
              <p className="mt-1 text-sm text-gray-500">
                Separate multiple areas with commas
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

export default MentorProfile;