# 🎓 EduVault LMS

> A complete Learning Management System — REST API, No Front End

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)]()
[![MySQL](https://img.shields.io/badge/MySQL-PostgreSQL-4479A1?style=flat-square&logo=mysql&logoColor=white)]()
[![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-85EA2D?style=flat-square&logo=swagger&logoColor=black)]()
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker&logoColor=white)]()
[![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red?style=flat-square)]()

---

## 📖 Overview

**EduVault LMS** is a complete REST API for an educational platform that supports:

| | |
|---|---|
| 🎯 **Goal** | Manage courses, enroll students, track progress, run quizzes, and auto-issue certificates |
| 👥 **Roles** | `ADMIN` controls all content and users, `STUDENT` interacts with content and tracks their own progress |
| 🔐 **Security** | JWT Authentication + Spring Security, every endpoint protected by role, refresh tokens + logout blacklist |
| 📄 **Documentation** | Swagger UI / OpenAPI 3 — auto-generated docs and in-browser API testing |

---

## 🧩 System Architecture

```mermaid
flowchart TB
    Client["📱 Client / Swagger UI"] -->|HTTP Request + JWT| Filter["JwtAuthFilter"]
    Filter --> Security["SecurityConfig<br/>(Role-based Access)"]
    Security --> Controller["REST Controllers"]
    Controller --> Service["Service Layer<br/>(Business Logic)"]
    Service --> Repo["Repository Layer<br/>(Spring Data JPA)"]
    Repo --> DB[("MySQL / PostgreSQL")]

    Service --> Cloudinary["☁️ Cloudinary<br/>(File Upload)"]
    Service --> Mail["📧 JavaMail<br/>(Email Service)"]
    Service --> PDF["📄 iText PDF<br/>(Certificates)"]

    style Client fill:#3b82f6,color:#fff
    style DB fill:#10b981,color:#fff
    style Cloudinary fill:#f59e0b,color:#fff
    style Mail fill:#f59e0b,color:#fff
    style PDF fill:#f59e0b,color:#fff
```

---

## 👥 Role-Based Permissions

```mermaid
flowchart LR
    subgraph Admin["🛡️ ADMIN"]
        A1[Create / edit / delete courses]
        A2[Publish or hide a course]
        A3[Manage all users]
        A4[Add lessons and quizzes]
        A5[View full platform statistics]
        A6[Upload files - Cloudinary]
        A7[View all students' quiz results]
    end

    subgraph Student["🎓 STUDENT"]
        S1[Browse published courses]
        S2[Enroll in courses]
        S3[Watch lessons]
        S4[Track completion progress]
        S5[Take quizzes]
        S6[Download PDF certificate]
        S7[Edit own profile]
    end

    style Admin fill:#3b82f620,stroke:#3b82f6
    style Student fill:#10b98120,stroke:#10b981
```

---

## 🗄️ Database Schema (ERD)

```mermaid
erDiagram
    USER ||--o{ COURSE : creates
    USER ||--o{ ENROLLMENT : has
    USER ||--o{ LESSON_PROGRESS : tracks
    USER ||--o{ QUIZ_RESULT : submits
    USER ||--o{ CERTIFICATE : earns
    COURSE ||--o{ LESSON : contains
    COURSE ||--o{ ENROLLMENT : has
    COURSE ||--o{ QUIZ : contains
    COURSE ||--o{ CERTIFICATE : issues
    LESSON ||--o{ LESSON_PROGRESS : tracked_by
    QUIZ ||--o{ QUESTION : contains
    QUIZ ||--o{ QUIZ_RESULT : produces

    USER {
        UUID id PK
        string name
        string email UK
        string password
        enum role
        boolean isActive
        timestamp createdAt
    }
    COURSE {
        UUID id PK
        string title
        text description
        string thumbnailUrl
        decimal price
        boolean isPublished
        UUID createdBy FK
    }
    LESSON {
        UUID id PK
        string title
        string videoUrl
        text content
        int duration
        int orderIndex
        UUID courseId FK
    }
    ENROLLMENT {
        UUID id PK
        UUID studentId FK
        UUID courseId FK
        timestamp enrolledAt
        enum status
        float completionPct
    }
    LESSON_PROGRESS {
        UUID id PK
        UUID studentId FK
        UUID lessonId FK
        boolean isCompleted
        timestamp watchedAt
        int watchedSeconds
    }
    QUIZ {
        UUID id PK
        string title
        UUID courseId FK
        int passMark
        int timeLimitMin
    }
    QUESTION {
        UUID id PK
        UUID quizId FK
        text text
        json options
        int correctIndex
        int points
    }
    QUIZ_RESULT {
        UUID id PK
        UUID studentId FK
        UUID quizId FK
        int score
        boolean isPassed
        int attemptNumber
        timestamp takenAt
    }
    CERTIFICATE {
        UUID id PK
        UUID studentId FK
        UUID courseId FK
        string pdfUrl
        timestamp issuedAt
        string uniqueCode UK
    }
```

---

## 🔐 Authentication Flow

```mermaid
sequenceDiagram
    participant U as Client
    participant A as AuthController
    participant S as AuthService
    participant DB as Database
    participant J as JwtTokenProvider

    U->>A: POST /api/auth/register
    A->>S: register(dto)
    S->>DB: save(User + hashed password)
    DB-->>S: User saved
    S-->>A: success
    A-->>U: 201 Created

    U->>A: POST /api/auth/login
    A->>S: login(credentials)
    S->>DB: findByEmail()
    DB-->>S: User
    S->>J: generateAccessToken() + generateRefreshToken()
    J-->>S: tokens
    S->>DB: store refresh token
    S-->>A: AuthResponse
    A-->>U: 200 OK {accessToken, refreshToken}

    U->>A: POST /api/auth/refresh-token
    A->>J: validate & rotate token
    J-->>A: new accessToken
    A-->>U: 200 OK

    U->>A: POST /api/auth/logout
    A->>S: blacklist(token)
    S->>DB: mark token blacklisted
    A-->>U: 200 OK
```

---

## 🔄 Student Lifecycle (Enrollment → Certificate)

```mermaid
stateDiagram-v2
    [*] --> Browsing: Browse published courses
    Browsing --> Enrolled: POST /enrollments/{courseId}
    Enrolled --> Watching: Watch lessons
    Watching --> Watching: Update LessonProgress
    Watching --> TakingQuiz: Take quizzes
    TakingQuiz --> Watching: Not yet complete
    Watching --> Completed: completionPct = 100%
    Completed --> CertificateIssued: Generate PDF + upload to Cloudinary
    CertificateIssued --> EmailSent: Send certificate by email
    EmailSent --> [*]
```

---

## 📡 API Endpoints

### 🔑 Auth
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/auth/register` | Public |
| `POST` | `/api/auth/login` | Public |
| `POST` | `/api/auth/refresh-token` | Public |
| `POST` | `/api/auth/logout` | Auth |

### 📚 Courses
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/courses` | Public |
| `GET` | `/api/courses/{id}` | Public |
| `POST` | `/api/courses` | Admin |
| `PUT` | `/api/courses/{id}` | Admin |
| `DELETE` | `/api/courses/{id}` | Admin |
| `PATCH` | `/api/courses/{id}/publish` | Admin |

### 🎬 Lessons
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/courses/{id}/lessons` | Student |
| `POST` | `/api/courses/{id}/lessons` | Admin |
| `PUT` | `/api/lessons/{id}` | Admin |
| `DELETE` | `/api/lessons/{id}` | Admin |
| `PATCH` | `/api/lessons/{id}/reorder` | Admin |

### 📋 Enrollments
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/enrollments/{courseId}` | Student |
| `GET` | `/api/enrollments/my` | Student |
| `DELETE` | `/api/enrollments/{courseId}` | Student |
| `GET` | `/api/enrollments/course/{id}` | Admin |

### 📈 Progress
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/progress/lessons/{id}/complete` | Student |
| `GET` | `/api/progress/courses/{id}` | Student |
| `GET` | `/api/progress/my` | Student |

### ❓ Quizzes
| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/quizzes` | Admin |
| `GET` | `/api/quizzes/{id}` | Student |
| `POST` | `/api/quizzes/{id}/submit` | Student |
| `GET` | `/api/quizzes/{id}/results` | Student |
| `GET` | `/api/quizzes/{id}/all-results` | Admin |

### 🎖️ Certificates
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/certificates/my` | Student |
| `GET` | `/api/certificates/{id}/download` | Student |
| `GET` | `/api/certificates/verify/{code}` | Public |

### 📊 Admin Stats
| Method | Endpoint | Access |
|---|---|---|
| `GET` | `/api/admin/stats` | Admin |
| `GET` | `/api/admin/users` | Admin |
| `PATCH` | `/api/admin/users/{id}/toggle` | Admin |
| `GET` | `/api/admin/courses/stats` | Admin |

---

## 📁 Project Structure

```
eduvault-lms/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── src/main/
    ├── resources/
    │   ├── application.yml
    │   └── application-prod.yml
    └── java/com/eduvault/lms/
        ├── EduVaultApplication.java
        │
        ├── config/                    # Spring configuration
        │   ├── SecurityConfig.java
        │   ├── JwtConfig.java
        │   ├── CloudinaryConfig.java
        │   └── SwaggerConfig.java
        │
        ├── security/                  # JWT + Filters
        │   ├── JwtTokenProvider.java
        │   ├── JwtAuthFilter.java
        │   └── UserDetailsServiceImpl.java
        │
        ├── model/                     # JPA Entities
        │   ├── User.java
        │   ├── Course.java
        │   ├── Lesson.java
        │   ├── Enrollment.java
        │   ├── LessonProgress.java
        │   ├── Quiz.java
        │   ├── Question.java
        │   ├── QuizResult.java
        │   └── Certificate.java
        │
        ├── enums/
        │   ├── Role.java              # ADMIN, STUDENT
        │   └── EnrollmentStatus.java
        │
        ├── repository/                # JPA Repositories
        │   ├── UserRepository.java
        │   ├── CourseRepository.java
        │   ├── LessonRepository.java
        │   ├── EnrollmentRepository.java
        │   ├── LessonProgressRepository.java
        │   ├── QuizRepository.java
        │   ├── QuizResultRepository.java
        │   └── CertificateRepository.java
        │
        ├── dto/
        │   ├── request/
        │   │   ├── RegisterRequest.java
        │   │   ├── LoginRequest.java
        │   │   ├── CourseRequest.java
        │   │   ├── LessonRequest.java
        │   │   └── QuizSubmitRequest.java
        │   └── response/
        │       ├── AuthResponse.java
        │       ├── CourseResponse.java
        │       ├── ProgressResponse.java
        │       ├── QuizResultResponse.java
        │       └── AdminStatsResponse.java
        │
        ├── service/                   # Business Logic
        │   ├── AuthService.java
        │   ├── CourseService.java
        │   ├── LessonService.java
        │   ├── EnrollmentService.java
        │   ├── ProgressService.java
        │   ├── QuizService.java
        │   ├── CertificateService.java
        │   ├── EmailService.java
        │   ├── FileUploadService.java
        │   └── AdminService.java
        │
        ├── controller/                # REST Controllers
        │   ├── AuthController.java
        │   ├── CourseController.java
        │   ├── LessonController.java
        │   ├── EnrollmentController.java
        │   ├── ProgressController.java
        │   ├── QuizController.java
        │   ├── CertificateController.java
        │   └── AdminController.java
        │
        └── exception/                 # Global Exception Handling
            ├── GlobalExceptionHandler.java
            ├── ResourceNotFoundException.java
            ├── UnauthorizedException.java
            └── AlreadyEnrolledException.java
```

---

## 🛠️ Tech Stack

| Technology | Purpose | Details |
|---|---|---|
| **Spring Boot** | Core framework | v6.x / Java 21 |
| **Spring Security** | Auth & authorization | JWT + BCrypt |
| **Spring Data JPA** | Database access | Hibernate ORM |
| **MySQL** | Primary database | v8.x / or PostgreSQL |
| **Swagger UI** | Auto-generated API docs | SpringDoc OpenAPI 3 |
| **Cloudinary** | File & image uploads | Java SDK |
| **JavaMail** | Sending emails | Spring Mail + Gmail |
| **iText PDF** | Certificate generation | v7.x |
| **MapStruct** | Entity → DTO mapping | v1.5.x |
| **Validation** | Input validation | Jakarta Bean Validation |
| **JUnit 5** | Unit testing | + Mockito |
| **Docker** | Deployment | + Docker Compose |

---

## 🔒 Security Design

| Element | Details |
|---|---|
| **JWT Secret** | Stored in `application.yml` (env variable) |
| **Access Token** | 15-minute validity |
| **Refresh Token** | 7-day validity — stored in DB |
| **Password Hashing** | `BCryptPasswordEncoder` (strength=12) |
| **Role Check** | `@PreAuthorize("hasRole('ADMIN')")` on every endpoint |
| **Logout** | Token blacklist stored in DB |
| **CORS** | Defined in `SecurityConfig` — allows all origins in dev |
| **Exception Handling** | `@RestControllerAdvice` via `GlobalExceptionHandler` |

---

## 🗺️ Build Roadmap

```mermaid
flowchart LR
    S1["1️⃣ Project Setup"] --> S2["2️⃣ Database & Entities"]
    S2 --> S3["3️⃣ Auth System (JWT)"]
    S3 --> S4["4️⃣ Course & Lesson CRUD"]
    S4 --> S5["5️⃣ Enrollment System"]
    S5 --> S6["6️⃣ Progress Tracking"]
    S6 --> S7["7️⃣ Quiz System"]
    S7 --> S8["8️⃣ Certificate Generation"]
    S8 --> S9["9️⃣ Admin Dashboard API"]
    S9 --> S10["🔟 Testing & Docker"]

    style S1 fill:#3b82f6,color:#fff
    style S10 fill:#10b981,color:#fff
```

<details>
<summary><strong>📋 Step-by-step details</strong></summary>

1. **Project Setup** — Spring Initializr: add (Web, Security, JPA, Mail, Validation, MySQL Driver). Use Java 21 + Maven. Package name: `com.eduvault.lms`
2. **Database & Entities** — Create all entities with relationships (OneToMany, ManyToOne). Set `spring.jpa.hibernate.ddl-auto=update` initially.
3. **Auth System (JWT)** — Build `JwtTokenProvider` + `JwtAuthFilter` + `SecurityConfig`. Implement register and login endpoints and verify tokens work correctly.
4. **Course & Lesson CRUD** — Build `CourseController` + `CourseService` + `Repository`. Add `@PreAuthorize` to Admin endpoints. Link Lesson to Course with `orderIndex`.
5. **Enrollment System** — Prevent duplicate enrollment with a custom exception. Ensure a Student can only see lessons if enrolled. Auto-calculate `completionPct`.
6. **Progress Tracking** — Every time a Student finishes a lesson → create a `LessonProgress` record. Calculate completion: `(completedLessons / totalLessons) * 100`. At 100% → trigger certificate generation.
7. **Quiz System** — Admin creates a Quiz with questions. Student takes it and submits answers. Service auto-grades, saves the result, and returns `isPassed`.
8. **Certificate Generation** — Use iText to generate a PDF with student + course data. Upload it to Cloudinary. Send it by email via JavaMail. Generate a `uniqueCode` for verification.
9. **Admin Dashboard API** — Statistics: number of students, most enrolled course, completion rate, total quizzes. All via custom JPQL queries.
10. **Testing & Docker** — Write unit tests for services using Mockito. Add Swagger annotations. Create Dockerfile + docker-compose.yml with a MySQL container.

</details>

---

## 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/<username>/eduvault-lms.git
cd eduvault-lms

# Run with Docker Compose
docker-compose up --build

# Or run locally with Maven
mvn spring-boot:run
```

Once running, the API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

---

## 📄 License

**© 2026 EduVault LMS — All Rights Reserved.**

This project is shared publicly for **viewing and demonstration purposes only**
(e.g., as part of a portfolio or educational showcase). No permission is
granted to copy, modify, distribute, deploy, or create derivative works from
this code without prior written consent from the copyright holder.

See the [`LICENSE`](./LICENSE) file for full terms.

---

<div align="center">

**EduVault LMS** · Spring Boot 6 · Java 21 · REST API · Back End Only

</div>
