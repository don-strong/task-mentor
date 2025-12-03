# Task Mentor

> A web application that connects college students with professionals through structured, task-based mentorship

**Live Demo:** [https://task-mentor.netlify.app](https://task-mentor.netlify.app) *(if deployed)*  
**Repository:** [https://github.com/don-strong/task-mentor](https://github.com/don-strong/task-mentor)  
**Video Demonstration:** *(Insert URL here)*

---

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Team Members](#team-members)
- [Features Implemented](#features-implemented)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [Repository Structure](#repository-structure)
- [Tech Stack](#tech-stack)
- [Known Issues](#known-issues)

---

## 📘 Project Overview

### Problem Statement
Traditional mentorship is time-consuming and often inefficient. Students struggle to find mentors for specific needs, and professionals lack structured ways to offer their expertise.

### Our Solution
Task Mentor is a marketplace where mentors offer a "task menu" of specific services (e.g., "Resume Review - 30 min", "Mock Interview - 60 min"). Students can browse mentors, view their offerings, and book exactly the help they need.

### Target Users
- **Students:** College students seeking career guidance, resume reviews, interview prep, or professional advice
- **Mentors:** Working professionals who want to monetize their expertise through time-bounded sessions

---

## 👥 Team Members

| Name | Role | GitHub | Key Contributions |
|------|------|--------|------------------|
| Tevita Mathias | Full-Stack Developer | [@aldohl95](https://github.com/aldohl95) | Search functionality, authentication UI |
| Vietnam (Michael) Ngo | Frontend Developer | [@vietnamngo](https://github.com/vietnamngo) | UI/UX design, React components |
| Tyson Ringelstetter | Backend Developer | [@TysonnR](https://github.com/TysonnR) | Spring Boot API, profile controllers |
| James No | QA / Documentation | [@james-no](https://github.com/james-no) | Testing, documentation, deployment |

---

## ✅ Features Implemented

### MVP Features (Core Functionality)

#### Authentication & User Management
- [x] User registration for students and mentors
- [x] Secure login with JWT authentication
- [x] Session persistence
- [x] Role-based access control (Student vs Mentor)
- [x] Logout functionality

#### Student Features
- [x] Student profile creation and editing
- [x] Browse and search available mentors
- [x] View mentor public profiles
- [x] View mentor's task offerings
- [x] Book sessions with mentors
- [x] View booking dashboard
- [x] Cancel bookings

#### Mentor Features
- [x] Mentor profile creation and editing
- [x] Create and manage task offerings (with images)
- [x] View booking requests dashboard
- [x] Accept/decline booking requests
- [x] View booking history
- [x] Public profile page for discovery

#### Search & Discovery
- [x] Advanced mentor search with filters
- [x] Filter by industry, expertise, experience level
- [x] Search by name or keyword
- [x] Category-based task browsing

### Additional Features
- [x] Responsive UI design (desktop-first)
- [x] Image upload for task offerings
- [x] File storage system
- [x] Global error handling
- [x] Input validation on all forms

### Future Enhancements (Not Implemented)
- [ ] Email notifications for bookings
- [ ] Payment processing integration
- [ ] Rating and review system
- [ ] Messaging between students and mentors
- [ ] Calendar integration
- [ ] Mobile-responsive design

---

## 🖥️ System Requirements

### Required Software

| Component | Version | Download Link |
|-----------|---------|---------------|
| **Java JDK** | 17 or higher | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **Node.js** | 18.x or higher | [nodejs.org](https://nodejs.org/) |
| **PostgreSQL** | 14 or higher | [postgresql.org](https://www.postgresql.org/download/) |
| **Git** | Latest | [git-scm.com](https://git-scm.com/) |
| **Maven** | 3.8+ (included with wrapper) | - |

### Operating System
- **macOS** 10.15+ (tested)
- **Windows** 10/11 (should work)
- **Linux** (Ubuntu 20.04+ recommended)

### Browser Requirements
- Chrome, Firefox, Safari, or Edge (latest version)

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/don-strong/task-mentor.git
cd task-mentor
```

### Step 2: Database Setup

#### 2.1 Install PostgreSQL
Follow the instructions for your OS from [postgresql.org](https://www.postgresql.org/download/)

#### 2.2 Create Database

```bash
# Start PostgreSQL service (if not running)
# macOS with Homebrew:
brew services start postgresql@14

# Windows: Use pgAdmin or Services

# Create database
psql postgres -c "CREATE DATABASE taskmentor;"
psql postgres -c "CREATE USER postgres WITH PASSWORD 'password';"
psql postgres -c "GRANT ALL PRIVILEGES ON DATABASE taskmentor TO postgres;"
```

**Note:** If you use a different username/password, update `backend/task-mentor/src/main/resources/application.properties`

### Step 3: Backend Setup

```bash
# Navigate to backend directory
cd backend/task-mentor

# Make Maven wrapper executable (macOS/Linux only)
chmod +x ./mvnw

# Install dependencies and build
./mvnw clean install -DskipTests

# Run the backend server
./mvnw spring-boot:run
```

**Windows users:** Replace `./mvnw` with `mvnw.cmd`

The backend will start on **http://localhost:8080**

#### 3.1 Verify Backend is Running

Open a new terminal and run:
```bash
curl http://localhost:8080/api/health
# Should return: {"status":"UP"}
```

### Step 4: Frontend Setup

```bash
# Open a NEW terminal window (keep backend running)
# Navigate to frontend directory from project root
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on **http://localhost:5173**

#### 4.1 Access the Application

Open your browser and navigate to:
```
http://localhost:5173
```

### Step 5: Create Test Accounts

#### Register as a Student:
1. Click "Register" on homepage
2. Select "Student" role
3. Fill in registration form
4. After login, create your student profile

#### Register as a Mentor:
1. Click "Register" on homepage
2. Select "Mentor" role  
3. Fill in registration form
4. After login, create your mentor profile
5. Create 2-3 task offerings

---

## 📁 Repository Structure

```
task-mentor/
├── backend/
│   └── task-mentor/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/task_mentor/task_mentor/
│       │   │   │   ├── controller/    # REST API endpoints
│       │   │   │   ├── service/       # Business logic
│       │   │   │   ├── repository/    # Database access
│       │   │   │   ├── entity/        # JPA entities
│       │   │   │   ├── dto/           # Data transfer objects
│       │   │   │   └── security/      # Authentication
│       │   │   └── resources/
│       │   │       └── application.properties  # Database config
│       │   └── test/                  # Unit tests
│       ├── pom.xml                    # Maven dependencies
│       └── mvnw                       # Maven wrapper
├── frontend/
│   ├── src/
│   │   ├── components/     # Reusable React components
│   │   ├── pages/          # Page components
│   │   ├── services/       # API service layer
│   │   ├── context/        # React context (auth, etc.)
│   │   └── App.jsx         # Main app component
│   ├── public/             # Static assets
│   ├── package.json        # NPM dependencies
│   └── vite.config.js      # Vite configuration
├── docs/                   # Additional documentation
├── README.md              # This file
└── .gitignore
```

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17
- **Database:** PostgreSQL 14
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security + JWT
- **Build Tool:** Maven
- **File Storage:** Local filesystem

### Frontend  
- **Framework:** React 18
- **Build Tool:** Vite
- **Styling:** Tailwind CSS
- **Routing:** React Router v6
- **HTTP Client:** Axios
- **State Management:** React Context API

### DevOps & Tools
- **Version Control:** Git / GitHub
- **Deployment:** Render (backend), Netlify (frontend)
- **API Testing:** Postman
- **Database Tool:** pgAdmin / psql

---

## ⚠️ Known Issues

### Database Sequence Issue (Local Development Only)

**Symptom:** Error when creating new profiles: `duplicate key value violates unique constraint`

**Cause:** Hibernate sequences out of sync

**Solution 1 - Clean Database (Recommended):**
```bash
# Stop backend (Ctrl+C)
psql postgres -c "DROP DATABASE taskmentor;"
psql postgres -c "CREATE DATABASE taskmentor;"
# Restart backend - Hibernate recreates tables
```

**Solution 2 - Fix Sequences:**
```bash
psql taskmentor -c "
SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));
SELECT setval('students_student_id_seq', (SELECT COALESCE(MAX(student_id), 0) FROM students));
SELECT setval('mentors_mentor_id_seq', (SELECT MAX(mentor_id) FROM mentors));
SELECT setval('tasks_task_id_seq', (SELECT COALESCE(MAX(task_id), 0) FROM tasks));
SELECT setval('bookings_booking_id_seq', (SELECT COALESCE(MAX(booking_id), 0) FROM bookings));
"
```

**Note:** This issue does NOT affect fresh production deployments.

### Deployment Configuration

If deploying to Render/Netlify, you must configure:
- **Backend:** Set `DATABASE_URL` and `JWT_SECRET` environment variables
- **Frontend:** Set `VITE_API_BASE_URL` to your backend URL
- **CORS:** Update allowed origins in backend SecurityConfig

---

## 📚 Additional Documentation

- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Detailed local setup instructions
- **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Render/Netlify deployment guide  
- **[TESTING_SUMMARY_DEC2.md](TESTING_SUMMARY_DEC2.md)** - QA testing report
- **[JAMES_TECHNICAL_SUMMARY.md](JAMES_TECHNICAL_SUMMARY.md)** - Backend technical documentation

---

## 🎓 Academic Project Information

**Course:** CS 301 - Software Engineering  
**Institution:** *(Your University)*  
**Quarter:** Fall 2025  
**Final Submission:** December 10, 2025

---

## 📧 Contact

For questions or issues, please contact:
- **Team Lead:** don-strong (GitHub)
- **Repository Issues:** [GitHub Issues](https://github.com/don-strong/task-mentor/issues)

---

## 📄 License

This project was created for academic purposes as part of CS 301 coursework.

---

**Last Updated:** December 3, 2025  
**Project Status:** ✅ MVP Complete, Ready for Grading
