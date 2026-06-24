package org.back.dailymentor.session.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.back.dailymentor.session.entity.Session;
import org.back.dailymentor.session.entity.SessionState;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.repository.LearningSessionRepository;
import org.back.dailymentor.session.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {
  private final SessionRepository sessionRepository;
  private final LearningSessionRepository learningSessionRepository;

  public Session getOrCreateSession(String userId) {
    return findUserActiveSession(userId)
      .orElseGet(
        () -> {
          Session session = Session
            .builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .startTimestamp(Instant.now())
            .sessionState(SessionState.NOT_STARTED)
            .build();

          return learningSessionRepository.create(session);
        }
      );
  }

  public void save(UserSession session) {
    sessionRepository.save(session);
  }

  public void updateSessionState(Session session) {
    Optional<Session> sessionOptional = learningSessionRepository.findById(session.id());
    if (sessionOptional.isPresent()) {
      learningSessionRepository.update(session);
    } else {
      throw new IllegalArgumentException("Session with ID " + session.id() + " not found.");
    }
  }

  private Optional<Session> findUserActiveSession(String userId) {
    List<Session> sessions = learningSessionRepository.findByUserId(userId);

    return sessions
      .stream()
      .filter(session -> session.sessionState() != SessionState.COMPLETED)
      .max(Comparator.comparing(Session::startTimestamp));
  }
}
