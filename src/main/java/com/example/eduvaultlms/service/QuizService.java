package com.example.eduvaultlms.service;

import com.example.eduvaultlms.dto.request.AnswerRequest;
import com.example.eduvaultlms.dto.request.QuestionRequest;
import com.example.eduvaultlms.dto.request.QuizRequest;
import com.example.eduvaultlms.dto.request.QuizSubmitRequest;
import com.example.eduvaultlms.dto.response.QuestionResponse;
import com.example.eduvaultlms.dto.response.QuizResponse;
import com.example.eduvaultlms.dto.response.QuizResultResponse;
import com.example.eduvaultlms.exception.DuplicateResourceException;
import com.example.eduvaultlms.exception.ResourceNotFoundException;
import com.example.eduvaultlms.exception.UnauthorizedException;
import com.example.eduvaultlms.model.Course;
import com.example.eduvaultlms.model.Question;
import com.example.eduvaultlms.model.Quiz;
import com.example.eduvaultlms.model.QuizResult;
import com.example.eduvaultlms.model.User;
import com.example.eduvaultlms.repository.CourseRepository;
import com.example.eduvaultlms.repository.EnrollmentRepository;
import com.example.eduvaultlms.repository.QuizRepository;
import com.example.eduvaultlms.repository.QuizResultRepository;
import com.example.eduvaultlms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository       quizRepo;
    private final QuizResultRepository quizResultRepo;
    private final UserRepository       userRepo;
    private final CourseRepository     courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final EmailService         emailService;
    private final ProgressService      progressService;

    @Transactional
    public QuizResponse createQuiz(UUID courseId, QuizRequest request) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found: " + courseId));

        if (quizRepo.existsByCourseIdAndTitleIgnoreCase(course.getId(), request.getTitle())) {
            throw new DuplicateResourceException(
                    "A quiz with the title '" + request.getTitle() +
                            "' already exists for this course.");
        }

        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setTitle(request.getTitle());
        quiz.setPassMark(request.getPassMark());
        quiz.setTimeLimitMin(request.getTimeLimitMin());

        List<Question> questions = new ArrayList<>();
        for (QuestionRequest qr : request.getQuestions()) {
            Question q = new Question();
            q.setQuiz(quiz);
            q.setText(qr.getText());
            q.setOptions(qr.getOptions());
            q.setCorrectIndex(qr.getCorrectIndex());
            q.setPoints(qr.getPoints() != null ? qr.getPoints() : 1);
            questions.add(q);
        }
        quiz.setQuestions(questions);

        return toQuizResponse(quizRepo.save(quiz));
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuizForStudent(UUID quizId, String username) {

        Quiz quiz    = findQuizOrThrow(quizId);
        User student = findUserOrThrow(username);
        guardEnrolled(student.getId(), quiz.getCourse().getId());

        return toQuizResponse(quiz);
    }

    @Transactional
    public QuizResultResponse submitQuiz(UUID quizId,
                                         QuizSubmitRequest request,
                                         String username) {

        Quiz quiz    = findQuizOrThrow(quizId);
        User student = findUserOrThrow(username);
        guardEnrolled(student.getId(), quiz.getCourse().getId());

        int totalPossiblePoints = quiz.getQuestions().stream()
                .mapToInt(Question::getPoints)
                .sum();

        int earnedPoints = 0;
        for (AnswerRequest answer : request.getAnswers()) {

            Question question = quiz.getQuestions().stream()
                    .filter(q -> q.getId().equals(answer.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Question not found in this quiz: " + answer.getQuestionId()));

            if (answer.getSelectedIndex().equals(question.getCorrectIndex())) {
                earnedPoints += question.getPoints();
            }
        }

        int finalScore = totalPossiblePoints > 0
                ? BigDecimal.valueOf(earnedPoints)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPossiblePoints), 10, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue()
                : 0;

        int attempt = quizResultRepo.countByStudentIdAndQuizId(student.getId(), quizId) + 1;

        QuizResult result = new QuizResult();
        result.setStudent(student);
        result.setQuiz(quiz);
        result.setScore(finalScore);
        result.setPassed(finalScore >= quiz.getPassMark());
        result.setAttemptNumber(attempt);
        result.setTakenAt(LocalDateTime.now());

        QuizResult saved = quizResultRepo.save(result);

        emailService.sendQuizResultEmail(
                student.getEmail(),
                student.getName(),
                quiz.getTitle(),
                saved.getScore(),
                saved.isPassed()
        );

        if (saved.isPassed()) {
            progressService.updateEnrollmentCompletion(student, quiz.getCourse());
        }

        return toQuizResultResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuizResultResponse> getResultsForStudent(UUID quizId, String username) {

        findQuizOrThrow(quizId);
        User student = findUserOrThrow(username);

        return quizResultRepo
                .findByStudentIdAndQuizIdOrderByAttemptNumberAsc(student.getId(), quizId)
                .stream()
                .map(this::toQuizResultResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuizResultResponse> getAllResultsForQuiz(UUID quizId) {

        findQuizOrThrow(quizId);

        return quizResultRepo
                .findByQuizIdOrderByStudentIdAscAttemptNumberAsc(quizId)
                .stream()
                .map(this::toQuizResultResponse)
                .toList();
    }

    private QuizResponse toQuizResponse(Quiz quiz) {

        List<QuestionResponse> questionResponses = quiz.getQuestions()
                .stream()
                .map(q -> {
                    QuestionResponse qr = new QuestionResponse();
                    qr.setId(q.getId());
                    qr.setText(q.getText());
                    qr.setOptions(q.getOptions());
                    qr.setPoints(q.getPoints());
                    return qr;
                })
                .toList();

        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setCourseId(quiz.getCourse().getId());
        response.setPassMark(quiz.getPassMark());
        response.setTimeLimitMin(quiz.getTimeLimitMin());
        response.setQuestions(questionResponses);
        return response;
    }

    private QuizResultResponse toQuizResultResponse(QuizResult result) {
        QuizResultResponse response = new QuizResultResponse();
        response.setId(result.getId());
        response.setQuizId(result.getQuiz().getId());
        response.setQuizTitle(result.getQuiz().getTitle());
        response.setStudentId(result.getStudent().getId());
        response.setStudentName(result.getStudent().getName());
        response.setScore(result.getScore());
        response.setPassed(result.isPassed());
        response.setAttemptNumber(result.getAttemptNumber());
        response.setTakenAt(result.getTakenAt());
        return response;
    }

    private void guardEnrolled(UUID studentId, UUID courseId) {
        if (!enrollmentRepo.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new UnauthorizedException(
                    "You must be enrolled in this course to access its quizzes.");
        }
    }

    private Quiz findQuizOrThrow(UUID id) {
        return quizRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + id));
    }

    private User findUserOrThrow(String username) {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}