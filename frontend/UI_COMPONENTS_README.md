# Task Mentor - UI Components Documentation

## Overview
This document describes the UI components created for the Task Mentor project, organized by task requirements.

## Project Location
All components have been created in: `/Users/jamesno/taskmentor_mike/`

## Created Components

### Task 3: Basic Registration and Login UI Components

#### 1. Register Component (`src/components/Register.jsx`)
- **Purpose**: User registration for both students and mentors
- **Features**:
  - User type selection (Student/Mentor)
  - Personal information fields (First Name, Last Name, Email)
  - Password confirmation with validation
  - Form validation with error messages
  - Responsive design with Tailwind CSS
- **Route**: `/register`

#### 2. Login Component (`src/components/Login.jsx`)
- **Purpose**: User authentication
- **Features**:
  - Email and password fields
  - "Remember Me" checkbox
  - "Forgot Password" link
  - Form validation
  - Link to registration page
- **Route**: `/login`

### Task 4: Profile Forms and UI Components

#### 3. StudentProfile Component (`src/components/StudentProfile.jsx`)
- **Purpose**: Student profile management
- **Features**:
  - View/Edit mode toggle
  - Personal information section
  - Academic information (University, Major, Graduation Year)
  - Bio text area
  - Dynamic interests list (add/remove)
  - Social links (LinkedIn, GitHub)
  - Form validation
  - Save/Cancel actions
- **Route**: `/student-profile`

#### 4. MentorProfile Component (`src/components/MentorProfile.jsx`)
- **Purpose**: Mentor profile management
- **Features**:
  - View/Edit mode toggle
  - Personal information section
  - Professional information (Title, Company, Years of Experience)
  - Availability selection
  - Professional bio
  - Dynamic expertise areas (add/remove)
  - Professional links (LinkedIn, Website)
  - Form validation
  - Save/Cancel actions
- **Route**: `/mentor-profile`

### Task 8: Task Creation UI and Public Mentor Profile Display

#### 5. TaskCreation Component (`src/components/TaskCreation.jsx`)
- **Purpose**: Mentor task menu management
- **Features**:
  - Create new tasks with form
  - Task fields: Title, Description, Category, Duration, Price
  - Predefined categories dropdown
  - List existing tasks with details
  - Delete tasks functionality
  - Form validation
  - Responsive grid layout
- **Route**: `/task-creation`

#### 6. PublicMentorProfile Component (`src/components/PublicMentorProfile.jsx`)
- **Purpose**: Public-facing mentor profile for students to browse
- **Features**:
  - Mentor header with profile information
  - Professional bio display
  - Expertise tags
  - Availability information
  - Social links (LinkedIn, Website)
  - Available sessions grid with task cards
  - Task details: Title, Description, Category, Duration, Price
  - "Book Session" buttons (placeholder functionality)
  - Reviews section (placeholder)
- **Route**: `/mentor/:id`

## Additional Components

### Home Component (`src/App.jsx`)
- Landing page with welcome message
- Call-to-action buttons (Get Started, Sign In)
- Demo links to all components for easy testing
- Responsive grid layout

## Technology Stack

- **React**: v19.1.1
- **React Router**: v6 (for navigation)
- **Tailwind CSS**: v4.1.16 (for styling)
- **Vite**: v7.1.7 (build tool)

## Setup and Running

1. Navigate to the project directory:
   ```bash
   cd /Users/jamesno/taskmentor_mike/frontend
   ```

2. Install dependencies (if not already done):
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open your browser and navigate to the URL shown in the terminal (typically `http://localhost:5173`)

## Routes Summary

| Route | Component | Description |
|-------|-----------|-------------|
| `/` | Home | Landing page with component demos |
| `/register` | Register | User registration |
| `/login` | Login | User login |
| `/student-profile` | StudentProfile | Student profile management |
| `/mentor-profile` | MentorProfile | Mentor profile management |
| `/task-creation` | TaskCreation | Task menu management |
| `/mentor/:id` | PublicMentorProfile | Public mentor profile display |

## Design Patterns

All components follow these patterns:
- **Form Validation**: Client-side validation with error messages
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Accessibility**: Proper labels, ARIA attributes where applicable
- **State Management**: React hooks (useState) for local state
- **TODO Comments**: Placeholder comments for API integration

## Next Steps

The UI components are ready for integration with the backend API. Look for `// TODO:` comments in the code for areas that need API integration:

1. Replace form submission alerts with actual API calls
2. Fetch user data from backend APIs
3. Implement JWT token management for authentication
4. Add loading states and error handling
5. Implement booking functionality
6. Add reviews and ratings system

## Notes

- All components use mock/hardcoded data for demonstration purposes
- Form submissions currently show alerts instead of making API calls
- Components are styled consistently with indigo/gray color scheme
- All components are responsive and work on mobile devices
