package org.back.dailymentor.session.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.back.dailymentor.session.entity.Session;

/**
 * Repository contract for learning session persistence.
 */
public interface LearningSessionRepository {
  /**
   * Creates a new learning session.
   *
   * @throws IllegalArgumentException when a session with the same id already exists
   */
  Session create(Session session);

  Optional<Session> findById(UUID id);

  List<Session> findByUserId(String userId);

  /**
   * Updates an existing learning session.
   *
   * @throws org.back.dailymentor.session.exception.SessionNotFoundException when the session does not exist
   */
  Session update(Session session);

  /**
   * Deletes an existing learning session.
   *
   * @throws org.back.dailymentor.session.exception.SessionNotFoundException when the session does not exist
   */
  void deleteById(UUID id);
}
