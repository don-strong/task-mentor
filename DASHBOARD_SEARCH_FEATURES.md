# Dashboard and Search Features Documentation

# Overview
This document describes the newly implemented Dashboard and Search pages for the Task Mentor application.

# Features Implemented

# 1. Dashboard Page (`/dashboard` and `/mentor-dashboard`)

# Location
- `frontend/src/pages/Dashboard.jsx`

# Features
- *Role-based Dashboard: Different views for students and mentors
- *Statistics Cards: 
  - Total Bookings
  - Pending Bookings
  - Accepted Bookings
  - Completed/Tasks Count (role-dependent)
  
- **Booking Management**:
  - View all bookings with status filter (PENDING, ACCEPTED, DECLINED, CANCELLED, COMPLETED)
  - Accept/Decline bookings (Mentor only)
  - Cancel bookings (Both roles)
  - Real-time status badges with color coding
  - Scheduled time display

- *Task Management (Mentor Only):
  - View all created tasks
  - Task details including category, duration, and images
  - Quick link to create new tasks

# Routes
- `/dashboard` - Student dashboard (protected)
- `/mentor-dashboard` - Mentor dashboard (protected)

#### Key Functions
- `fetchDashboardData()` - Loads bookings and tasks
- `handleAcceptBooking()` - Mentor accepts booking
- `handleDeclineBooking()` - Mentor declines booking
- `handleCancelBooking()` - Cancel booking (any role)
- `getStatusBadge()` - Renders color-coded status badges
- `getStatistics()` - Calculates dashboard statistics

---

# 2. Search Page (`/search`)

# Location
- `frontend/src/pages/Search.jsx`

# Features
- **Multiple Search Types**:
  1. **Mentors Search** - Find mentors by various criteria
  2. **Tasks Search** - Find specific tasks
  3. **Students Search** - Find students (Mentor only)
  4. **Combined Search** - Find mentors with specific tasks

# Search Filters

# Mentor Filters
- Name (text search)
- Company (dropdown from API)
- Industry (text search)
- Expertise (text search)
- Min Years Experience (number)

# Task Filters
- Title (text search)
- Category (dropdown from API)
- Min Duration (minutes)
- Max Duration (minutes)

# Student Filters
- Name (text search)
- Major (dropdown from API)
- Graduation Year (number)
- Min Graduation Year (number)
- Career Interests (text search)

# Combined Filters (Mentors + Tasks)
- Mentor Name (text search)
- Expertise (text search)
- Task Category (dropdown from API)
- Max Duration (minutes)

# Display Features
- *Mentor Cards:
  - Profile photo or initial avatar
  - Name, role title, company
  - Bio preview
  - Years of experience badge
  - Task count badge
  - Expertise areas (first 3)
  - Click to navigate to profile

- *Task Cards:
  - Title and category badge
  - Description preview
  - Mentor name
  - Duration
  - Button to view mentor profile

- *Student Cards:
  - Initial avatar
  - Name, major, graduation year
  - Bio preview
  - Career interests (first 3)

# Key Functions
- `loadFilterOptions()` - Fetches filter options from API
- `handleSearch()` - Executes search based on current filters
- `clearFilters()` - Resets all filter values
- `renderMentorFilters()` - Renders mentor-specific filters
- `renderTaskFilters()` - Renders task-specific filters
- `renderStudentFilters()` - Renders student-specific filters
- `renderCombinedFilters()` - Renders combined search filters
- `renderMentorCard()` - Renders mentor result card
- `renderTaskCard()` - Renders task result card
- `renderStudentCard()` - Renders student result card

---

# Navigation Updates

# Navbar Updates
Updated `frontend/src/components/Navbar.jsx` to include:

# Desktop Navigation
*For Students:
- Dashboard
- Search
- My Profile

*For Mentors:
- Dashboard
- Search
- My Profile
- My Tasks

# Mobile Navigation
Same structure as desktop, optimized for mobile screens

# Home Page
Updated `frontend/src/App.jsx` Home component to include:
- "Browse Mentors" button linking to `/search`

---

# Routes Added

# App.jsx Routes
```javascript
// Public route
<Route path="/search" element={<Search />} />

// Protected student route
<Route path="/dashboard" element={
  <ProtectedRoute requiredRole="student">
    <Dashboard />
  </ProtectedRoute>
} />

// Protected mentor route
<Route path="/mentor-dashboard" element={
  <ProtectedRoute requiredRole="mentor">
    <Dashboard />
  </ProtectedRoute>
} />
```

---

# API Integration

# Dashboard APIs Used
- `bookingService.getBookingsByStudent(studentId)`
- `bookingService.getBookingsByMentor(mentorId)`
- `bookingService.acceptBooking(bookingId, mentorId)`
- `bookingService.declineBooking(bookingId, mentorId)`
- `bookingService.cancelBooking(bookingId, userId, userType)`
- `taskService.getTasksByMentor(mentorId)`

# Search APIs Used
- `searchService.searchMentors(filters)`
- `searchService.searchTasks(filters)`
- `searchService.searchStudents(filters)`
- `searchService.searchMentorsWithTasks(filters)`
- `searchService.getFilterOptions()`

---

# Styling
Both pages use Tailwind CSS with:
- Responsive grid layouts (1/2/3 columns based on screen size)
- Consistent color scheme (indigo primary, color-coded status badges)
- Hover effects and transitions
- Loading spinners
- Error message displays
- Mobile-first responsive design

---

# User Experience Features

# Dashboard
- Empty state messages with call-to-action buttons
- Loading state with spinner
- Error handling with clear messages
- Tab navigation between bookings and tasks (mentors)
- Status filtering for bookings
- Color-coded status badges
- Clear action buttons (Accept/Decline/Cancel)

# Search
- Search type selector buttons
- Filter persistence during type switch
- Clear all filters button
- Loading state during search
- Empty results message
- Result count display
- Responsive card grid
- Click-to-navigate on cards
- Auto-load filter options on mount
- Auto-search on type change

---

# Future Enhancements
1. Pagination for large result sets
2. Sort options (e.g., by date, rating)
3. Save search filters
4. Advanced filters (price range, availability)
5. Calendar view for bookings
6. Booking notifications
7. Export dashboard data
8. Search history
9. Favorite mentors/tasks
10. Booking reminders

---

# Testing Recommendations
1. Test both student and mentor dashboards
2. Test all search types with various filter combinations
3. Test booking actions (accept, decline, cancel)
4. Test responsive design on mobile devices
5. Test error states (network failures, empty data)
6. Test navigation between pages
7. Test protected route access
8. Test filter clearing functionality
9. Test loading states
10. Test with real backend data

---

# Files Created/Modified

# Created
- `frontend/src/pages/Dashboard.jsx` - Dashboard component
- `frontend/src/pages/Search.jsx` - Search component
- `DASHBOARD_SEARCH_FEATURES.md` - This documentation

# Modified
- `frontend/src/App.jsx` - Added routes and Home page updates
- `frontend/src/components/Navbar.jsx` - Added navigation links

---

# Dependencies
All features use existing dependencies:
- React hooks (useState, useEffect)
- react-router-dom (useNavigate, Link)
- AuthContext (useAuth)
- Existing service files (bookingService, taskService, searchService)
- Tailwind CSS for styling
