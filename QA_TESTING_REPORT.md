# QA Testing Report - Task Mentor Application

**Date:** November 26, 2025  
**Tester:** James No  
**Environment:** Local development (macOS)  
**Tested PRs:** PR #19 (Tests), PR #21 (Frontend)

---

## Executive Summary

Testing revealed **3 critical/high severity bugs** that significantly impact user experience and block core functionality. While basic authentication and profile creation work, navigation and UI styling issues prevent users from accessing main features of the application.

---

## ✅ Working Features

### Authentication
- ✅ User registration (student account) - works after database fix
- ✅ User login with credentials
- ✅ Logout functionality
- ✅ Session persistence on profile page

### Profile Management
- ✅ Student profile creation form displays correctly
- ✅ Profile form fields are functional (Full Name, Bio, Major, Graduation Year, Career Interests, Profile Photo URL)
- ✅ "Create Profile" button successfully saves profile data
- ✅ "My Profile" navigation link works correctly

### UI/UX (Partial)
- ✅ Clean, professional UI design
- ✅ Responsive navigation bar
- ✅ User email and account type badge displayed in navbar
- ✅ Proper form structure and layout

---

## 🐛 Bugs Found

### 1. Input Text Visibility Issue
**Severity:** 🔴 **HIGH**  
**Status:** Not Fixed

**Description:**  
All text input fields throughout the application display white text on white background, making typed content invisible to users unless text is highlighted/selected.

**Affected Areas:**
- Login page (email and password fields)
- Registration page
- Student profile form (all input fields)
- Likely all other forms in the application

**Impact:**  
Major usability issue. Users cannot see what they're typing, making all form interactions frustrating and error-prone. This affects every user interaction with the application.

**Expected Behavior:**  
Input text should be dark colored (black or dark gray) for visibility against white background.

**Root Cause:**  
CSS styling issue - input elements missing proper `color` property or have incorrect value.

**Recommended Fix:**  
Frontend team (vietnamngo) should add proper text color styling to all input fields:
```css
input, textarea {
  color: #000000; /* or similar dark color */
}
```

---

### 2. Broken Navigation for Authenticated Users
**Severity:** 🔴 **CRITICAL**  
**Status:** Not Fixed

**Description:**  
After successful login, clicking "Home" or "Find Mentors" navigation links redirects users back to the login page, as if authentication was lost.

**Affected Areas:**
- Home navigation link
- Find Mentors navigation link

**Working Navigation:**
- My Profile ✅
- Logout ✅

**Impact:**  
Critical functionality blocker. Students cannot browse mentors or access the home page, which are core features of the application. Users are essentially locked to only their profile page after logging in.

**Expected Behavior:**  
Authenticated users should be able to navigate to all pages (Home, Find Mentors, My Profile) while staying logged in.

**Possible Root Causes:**
1. Authentication token not being properly included in navigation requests
2. Protected routes incorrectly configured or checking authentication
3. Token/session being cleared on route changes
4. Missing route definitions for authenticated users
5. Authentication context not being preserved across route changes

**Recommended Investigation:**
- Check browser console for errors during navigation
- Verify token persistence in LocalStorage/SessionStorage
- Review React Router protected route configuration
- Check AuthContext provider implementation
- Verify API authentication headers are being sent

**Recommended Fix:**  
Backend/Frontend teams need to investigate authentication state management and route protection logic.

---

### 3. Database Sequence Synchronization Issue
**Severity:** ⚠️ **MEDIUM** (occurred once, fixed manually)  
**Status:** Fixed (workaround applied)

**Description:**  
User registration initially failed with error: `duplicate key value violates unique constraint "users_pkey" Detail: Key (user_id)=(1) already exists`

**Root Cause:**  
Test users were inserted into the database manually without updating the auto-increment sequence, causing the application to attempt reusing existing IDs.

**Impact:**  
Blocks new user registration until manually fixed. This would prevent any new users from signing up in a production environment.

**Fix Applied:**
```sql
SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));
```

**Recommended Prevention:**
1. Always insert test data through the application's registration flow
2. If manual insertion is required, update sequences afterward
3. Add database migration/seed scripts that properly handle sequences
4. Consider adding better error handling in the registration endpoint to provide user-friendly messages

---

## 🧪 Test Coverage

### Tested User Flows
- ✅ Student registration
- ✅ Student login
- ✅ Student profile creation
- ✅ Logout
- ❌ Mentor browsing (blocked by navigation bug)
- ❌ Home page for authenticated users (blocked by navigation bug)
- ⏸️ Mentor registration (not tested)
- ⏸️ Task creation (not tested)
- ⏸️ Booking system (not tested)

### Not Tested (Blocked by Bugs)
- Mentor discovery/search
- Mentor profile viewing
- Task booking
- Session management
- File upload functionality
- Mentor account features

---

## 📊 Bug Priority Summary

| Severity | Count | Bugs |
|----------|-------|------|
| 🔴 Critical | 1 | Navigation broken for authenticated users |
| 🔴 High | 1 | Input text visibility issue |
| ⚠️ Medium | 1 | Database sequence issue (fixed) |

---

## 🎯 Recommendations

### Immediate Actions Required
1. **Fix input text color** (Frontend - vietnamngo)
   - Quick CSS fix, high impact on usability
   - Should be highest priority

2. **Fix navigation/authentication** (Frontend/Backend - vietnamngo, potentially backend team)
   - Critical functionality blocker
   - Prevents testing of core features
   - Requires investigation of authentication state management

3. **Implement proper database seeding** (Backend - TysonnR, james-no)
   - Create seed scripts that handle sequences correctly
   - Document proper test data insertion procedures

### Before Next Deployment
- [ ] All critical bugs must be fixed
- [ ] Re-test complete user flows for both student and mentor accounts
- [ ] Verify file upload functionality (from PR #17)
- [ ] Test booking system
- [ ] Cross-browser testing
- [ ] Mobile responsiveness testing

### Process Improvements
1. **Require visual QA review** before merging frontend PRs
2. **Test with actual user workflows** rather than just unit tests
3. **Set up proper test database seeding** to avoid sequence issues
4. **Consider adding E2E tests** for critical user flows
5. **Add authentication state debugging** tools for development

---

## 🔍 Additional Notes

### Environment Setup
- PostgreSQL database required configuration before running backend
- Database user and schema need manual setup
- README has been updated with proper setup instructions

### Positive Observations
- Backend API appears functional (successful registration/login)
- Database schema properly created via Hibernate
- UI design is clean and professional
- Basic authentication flow works correctly
- Profile creation successfully persists data

### Test Data in Database
Existing test accounts found:
- `student1@example.com` (password unknown)
- `mentor1@example.com` (password unknown)

New test account created during QA:
- `james.d.no@outlook.com` (student account)

---

## 👥 Assignees

| Bug | Owner | Priority |
|-----|-------|----------|
| Input text visibility | vietnamngo (Frontend) | 🔴 High |
| Navigation/Auth issue | vietnamngo + Backend team | 🔴 Critical |
| Database seeding | TysonnR, james-no (Backend) | ⚠️ Medium |

---

## Next Steps

1. Share this report with the team
2. Create GitHub issues for each bug
3. Backend team: Fix navigation/authentication
4. Frontend team: Fix input text color
5. Re-test after fixes are deployed
6. Continue testing mentor flows and advanced features

---

**Report prepared by:** James No  
**Contact:** james.d.no@outlook.com  
**GitHub:** @james-no
