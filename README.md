# JobTrackr API

> Production-grade REST API for JobTrackr — a full-stack job application tracking platform with AI-powered job data extraction.

🌐 **Live Demo:** [jobtrackr-sy.vercel.app](https://jobtrackr-sy.vercel.app)

---

## Features

- 🔐 **JWT Authentication** — Secure register/login with BCrypt password hashing and stateless JWT tokens
- 🤖 **AI Auto-fill** — Paste any job posting URL and extract company, role, salary, and summary automatically using LLM
- 📊 **Stats Engine** — Response rate, pipeline breakdown, and application counts per status
- 🔍 **Filtering & Search** — Filter applications by status, search by company or role
- ✅ **Input Validation** — Request validation with clean, structured error responses
- 🐳 **Docker Support** — Fully containerized with Docker and docker-compose

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| AI Integration | Groq API (LLaMA 3.1) |
| Containerization | Docker |
| Deployment | Render |

---

## API Endpoints

### Auth
```
POST /api/auth/register    — Create account
POST /api/auth/login       — Login, returns JWT token
```

### Applications (all protected — requires Bearer token)
```
GET    /api/applications           — Get all applications (filter by ?status= or ?company=)
POST   /api/applications           — Add new application
PUT    /api/applications/{id}      — Update application
DELETE /api/applications/{id}      — Delete application
GET    /api/applications/stats     — Get pipeline stats
```

### Job Extraction
```
POST /api/jobs/extract     — Extract job data from URL using AI
```

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- PostgreSQL
- Docker (optional)

### Run Locally

**1. Clone the repo**
```bash
git clone https://github.com/tywish-dev/jobtrackr.git
cd jobtrackr
```

**2. Create local properties file**

Create `src/main/resources/application-local.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jobtrackr
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
app.jwt.secret=your-secret-key-at-least-32-characters
app.cors.allowed-origins=http://localhost:3000
groq.api.key=your_groq_api_key
```

**3. Run**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

API is available at `http://localhost:8080`

---

### Run with Docker

```bash
# Build and start everything
docker-compose up --build
```

`docker-compose.yml` spins up the Spring Boot API and PostgreSQL together.

---

## Environment Variables (Production)

| Variable | Description |
|---|---|
| `DATABASE_URL` | JDBC PostgreSQL connection URL |
| `DATABASE_USERNAME` | Database username |
| `DATABASE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for signing JWT tokens (min 32 chars) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins |
| `GROQ_API_KEY` | Groq API key for AI job extraction |

---

## Project Structure

```
src/main/java/com/sametyilmaz/jobtrackr/
├── controller/         — REST controllers
├── service/            — Business logic
├── repository/         — JPA repositories
├── entity/             — JPA entities (User, Application)
├── dto/                — Request/response objects
├── security/           — JWT filter, Spring Security config
└── exception/          — Global exception handler
```

---

## AI Job Extraction

The `/api/jobs/extract` endpoint accepts any job posting URL and:

1. Fetches the page HTML
2. Strips markup to clean text
3. Sends to Groq LLaMA 3.1 with a structured extraction prompt
4. Returns JSON with company, role, salary, location, and summary

Works with Greenhouse, Lever, Workday, and most company career pages.

---

## Author

**Samet Yılmaz** — Backend Software Engineer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat&logo=linkedin&logoColor=white)](https://linkedin.com/in/samet-yilmaz-dev)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)](https://github.com/tywish-dev)

---

## License

MIT