
---

## 📘 Project Name
Task Mentor 
 
A web application that connects college students with professionals through structured, task-based mentorship
---

## 👥 Group Members / Contributors

| Name | Role | GitHub Username |
|------|------|-----------------|
| Tevita Mathias |  Developer | @aldohl95 |
| Vietnam(Michael) Ngo | Frontend Developer | @vietnamngo |
| Tyson Ringelstetter | Backend Developer | @TysonnR |
| James No | QA / Documentation | @james-no |


---

## 🧭 Brief Background About the Project
  
Task Mentor addresses the inefficiencies of traditional mentorship by
enabling specific, time-bounded interactions. 
Mentors offer a "task menu" of services (e.g., "Resume Review - 30 min"), 
and students book exactly the help they need.
---

## ⚙️ Current Features

-  Secure user authentication with JWT -**in progress**
-  Comprehensive student and mentor profiles -**in progress**
-  Task menu system for mentors to define offerings -**in progress**
-  Advanced mentor search and filtering -**in progress**
-  Session booking with email confirmations -**in progress**
-  Responsive design for desktop and mobile -**in progress**

---

## 🧩 Planned Features / Future Work

- Sesion Dashboard
- Rating & Review System
- Advanced Scheduling
- Messaging System

---

## 📅 Week-by-Week Plan
*(This should align with your project’s real timeline — update each week’s goals.)*

| **Week** | **Milestone / Deliverables** | **Owners** | **Evidence (PRs/Links/Demos)** |
|-----------|------------------------------|-------------|--------------------------------|
| 1 | Authentication & Database Foundation | Tean | Spring security, authentication UI, user login/registration |
| 2 | User Profiles | Team | Crud, Database persistence, UI profile Creation |
| 3 | Task System & public Profile | Team | Task creation/deletion, Task Categorization, UI layout for task display |
| 4 | Search & Discovery | Team | Mentor Discovery Page, Filters |
| 5 | Booking System | Team | Booking Requests, Proper Booking relationships, Mentor view booking |
| 6 | Booking Completion & Core Polish | Team | Booking Accept/Decline, Double Booking Prevention, Status updates in UI |
| 7 | Buffer Week | Team | Code cleanup, Paths tested, Main Features tested |
| 8 | Deployment preperation | Team |Staging enviornment functional, Video demo, final slides |

---

## 🧠 Definition of Done (DoD)
A feature is **done** when:
- Code is reviewed and merged into `main`  
- All acceptance criteria are met   
- Demo shows functional feature without breaking existing functionality  
- Documentation (README / inline comments) is up to date  

---

## 💻 How to Run

**Your Entry:**


### 1. Clone Repository
```
git clone https://github.com/don-strong/task-mentor.git
cd task-mentor
```

### 2. Backend Setup (Python/Flask)
```
TBD

### 3. Frontend Setup (React)
```
TBD
```
### 4. Configure Database Connection
   
   Navigate to: `backend/task-mentor/src/main/resources/`
   
   a. Copy the example file:
```bash
   cp application.properties.example application.properties
```
   
   b. Edit `application.properties` and update with YOUR local PostgreSQL credentials:
```properties
   spring.datasource.username=YOUR_POSTGRES_USERNAME
   spring.datasource.password=YOUR_POSTGRES_PASSWORD
```
   
   ⚠️ **IMPORTANT:** Never commit `application.properties` with real passwords! This file is in `.gitignore`.
