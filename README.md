# HireNest — Modern Job Portal

A full-stack job portal for fresher SDE portfolios. Applicants search and apply for jobs; recruiters post jobs and manage applications; real-time chat and notifications via WebSocket/STOMP.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Frontend | React (Vite), Tailwind CSS, React Router, Context API, SockJS/STOMP |
| Backend | Spring Boot 3, Spring Security, JWT, OAuth2, JPA, WebSocket |
| Database | MySQL |
| Files | Cloudinary |
| Deploy | Vercel (frontend), Render (backend), Docker |

## Project Structure

```
hirenest/
├── backend/          # Spring Boot API
│   └── src/main/java/com/hirenest/
│       ├── controller/   # REST endpoints
│       ├── service/      # Business logic
│       ├── repository/   # JPA repositories
│       ├── entity/       # Database entities
│       ├── dto/          # Request/response objects
│       ├── config/       # Security, WebSocket, CORS
│       ├── security/     # JWT & OAuth2
│       └── websocket/    # STOMP chat & notifications
├── frontend/         # React SPA
│   └── src/
│       ├── pages/        # Route pages
│       ├── components/   # Reusable UI
│       ├── context/      # Auth & notifications
│       └── api/          # Fetch client
├── database/         # Schema reference
└── docker-compose.yml
```

## Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.9+

## Database Setup

1. Start MySQL and create the database (or let Hibernate create it):

```sql
CREATE DATABASE IF NOT EXISTS hirenest;
```

2. Default credentials in `backend/src/main/resources/application.properties`:
   - Database: `hirenest`
   - Username: `root`
   - Password: `Naman@1424`

## Environment Variables

Copy examples and fill in your values:

```bash
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | Secret key for JWT signing (min 32 chars) |
| `FRONTEND_URL` | React app URL for CORS & OAuth redirect |
| `GOOGLE_CLIENT_ID/SECRET` | Google OAuth2 credentials |
| `GITHUB_CLIENT_ID/SECRET` | GitHub OAuth2 credentials |
| `CLOUDINARY_*` | Cloudinary upload credentials |
| `VITE_API_URL` | Backend URL for frontend |

### OAuth Redirect URLs

- Google: `http://localhost:8080/login/oauth2/code/google`
- GitHub: `http://localhost:8080/login/oauth2/code/github`

## Run Locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App: `http://localhost:5173`

## Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Applicant | applicant@hirenest.com | applicant123 |
| Recruiter | recruiter@hirenest.com | recruiter123 |
| Admin | admin@hirenest.com | admin123 |

## Key API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register applicant/recruiter |
| POST | `/api/auth/login` | Login, returns JWT |
| GET | `/api/jobs` | Search jobs (public) |
| POST | `/api/applicant/jobs/{id}/apply` | Apply for job |
| POST | `/api/recruiter/jobs` | Post job |
| GET | `/api/notifications` | List notifications |
| POST | `/api/chat/send` | Send message (REST) |
| WS | `/ws` | SockJS + STOMP endpoint |

## WebSocket Topics

- `/topic/notifications/{userId}` — real-time notifications
- `/topic/chat/{userId}` — incoming chat messages
- `/app/chat.send` — send message via STOMP

## Docker

```bash
docker-compose up --build
```

- Frontend: http://localhost
- Backend: http://localhost:8080
- MySQL: localhost:3306

## Deployment

### Frontend (Vercel)

1. Import `frontend/` repo
2. Set `VITE_API_URL` to your Render backend URL
3. Deploy

### Backend (Render)

1. Create Web Service from `backend/`
2. Set environment variables (DB URL, JWT, OAuth, Cloudinary)
3. Use Render MySQL or external MySQL

## Features Checklist

- [x] JWT + BCrypt authentication
- [x] Google & GitHub OAuth2
- [x] Roles: APPLICANT, RECRUITER, ADMIN
- [x] Job search, filter, sort, pagination
- [x] Apply, save jobs, track application status
- [x] Recruiter job CRUD & application management
- [x] Profile, resume & avatar upload (Cloudinary)
- [x] Real-time chat (WebSocket/STOMP)
- [x] Real-time notifications
- [x] Dark themed responsive UI

## Interview Talking Points

1. **Security**: Stateless JWT, role-based `@PreAuthorize`, BCrypt passwords, OAuth2 for social login.
2. **Architecture**: Layered backend (controller → service → repository), DTO pattern, global exception handler.
3. **Real-time**: STOMP over SockJS with topic-based pub/sub for chat and notifications.
4. **Search**: JPA Specifications for dynamic job filtering.
5. **Frontend**: Context API for auth state, protected routes, native Fetch with Bearer token.

## License

MIT — portfolio use.
# HireNest
