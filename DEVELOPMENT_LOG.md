# Task Mentor - Development Log

## Date: November 8, 2025

### Environment Setup
1. **Installed GitHub CLI (`gh`)**
   - Used `winget` to install GitHub CLI for managing pull requests
   - Command: `winget install --id GitHub.cli`

2. **Cloned Repository**
   - Cloned `don-strong/task-mentor` repository to local Documents folder
   - Location: `C:\Users\micha\Documents\Task mentor`
   - Repository contains:
     - Backend folder (Spring Boot application)
     - Frontend folder (React + Vite + Tailwind CSS)
     - README.md

### Current Task: Create Basic Registration and Login UI Components

#### Frontend Technology Stack
- **Framework**: React 19.1.1
- **Build Tool**: Vite 7.1.7
- **Styling**: Tailwind CSS 4.1.16
- **File Format**: `.jsx` (JavaScript + HTML)

#### Components Created

##### 1. Login Component (`src/components/Login.jsx`)
**Features:**
- Email input field (required, type: email)
- Password input field (required, type: password)
- Login button with form submission
- Link to registration page
- Responsive design with Tailwind CSS
- Form validation (client-side)
- State management using React hooks

**Styling:**
- Centered card layout with shadow
- Blue color scheme for primary actions
- Focus states for accessibility
- Responsive max-width design

##### 2. Registration Component (`src/components/Register.jsx`)
**Features:**
- Username input field (required)
- Email input field (required, type: email)
- Password input field (required, minimum 6 characters)
- Confirm password field (required, minimum 6 characters)
- Password matching validation
- Register button with form submission
- Link to login page
- Responsive design with Tailwind CSS
- Form validation (client-side)
- State management using React hooks

**Styling:**
- Consistent design with Login component
- Same blue color scheme
- Card-based layout
- Form spacing and accessibility features

### Backend API Integration

#### Backend Components Created

##### DTOs (Data Transfer Objects)
1. **LoginRequest.java** - Contains email and password for login
2. **RegisterRequest.java** - Contains username, email, password, and accountType for registration
3. **AuthResponse.java** - Returns userId, username, email, accountType, and message

##### Service Layer
**AuthService.java** - Business logic for authentication
- `register()` - Validates and creates new users
- `login()` - Authenticates existing users
- Checks for duplicate emails and usernames
- **Note**: Currently using plain text passwords (NOT SECURE - FOR DEVELOPMENT ONLY)
- **TODO**: Implement BCrypt password hashing

##### Controller Layer
**AuthController.java** - REST API endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login existing user
- CORS enabled for Vite dev server (localhost:5173)
- Returns appropriate HTTP status codes

#### Frontend Updates

##### Login Component
- ✅ Connected to `POST /api/auth/login` endpoint
- Uses async/await with fetch API
- Displays success/error messages with alerts
- Logs successful login data to console

##### Register Component
- ✅ Connected to `POST /api/auth/register` endpoint
- Added account type dropdown (Student/Mentor)
- Uses async/await with fetch API
- Validates password matching client-side
- Displays success/error messages with alerts

#### API Endpoints

**Base URL**: `http://localhost:8080/api/auth`

**POST /api/auth/register**
Request body:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "accountType": "student" or "mentor"
}
```

**POST /api/auth/login**
Request body:
```json
{
  "email": "string",
  "password": "string"
}
```

Response (both endpoints):
```json
{
  "userId": number,
  "username": "string",
  "email": "string",
  "accountType": "string",
  "message": "string"
}
```

#### Next Steps (TODO)
- [ ] Implement BCrypt password hashing in backend
- [ ] Implement routing (React Router) to navigate between Login and Register
- [ ] Add loading states during form submission
- [ ] Implement authentication state management (Context API or Redux)
- [ ] Store authentication token/session
- [ ] Redirect to dashboard after successful login
- [ ] Add "Remember Me" functionality to Login
- [ ] Implement "Forgot Password" feature
- [ ] Replace alerts with proper notification UI components
- [ ] Add form validation error messages inline

#### File Locations

**Frontend Components:**
- Login: `frontend\src\components\Login.jsx`
- Register: `frontend\src\components\Register.jsx`

**Backend Files:**
- AuthController: `backend\task-mentor\src\main\java\com\task_mentor\task_mentor\controller\AuthController.java`
- AuthService: `backend\task-mentor\src\main\java\com\task_mentor\task_mentor\service\AuthService.java`
- LoginRequest: `backend\task-mentor\src\main\java\com\task_mentor\task_mentor\dto\LoginRequest.java`
- RegisterRequest: `backend\task-mentor\src\main\java\com\task_mentor\task_mentor\dto\RegisterRequest.java`
- AuthResponse: `backend\task-mentor\src\main\java\com\task_mentor\task_mentor\dto\AuthResponse.java`

#### Commands to Run
```bash
# Navigate to frontend directory
cd "C:\Users\micha\Documents\Task mentor\frontend"

# Install dependencies (if not already done)
npm install

# Run development server
npm run dev

# Run linter
npm run lint

# Build for production
npm run build
```

---

## Notes
- All components use `.jsx` file extension as requested
- Components are styled with Tailwind CSS (already configured in the project)
- Forms include basic HTML5 validation
- Backend integration is pending (marked with TODO comments in code)
