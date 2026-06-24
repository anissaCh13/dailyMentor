package org.back.dailymentor.session.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * Domain entity representing a DailyMentor learning session.
 *
 * <p>The entity is immutable and framework-free by design. Persistence details, AI orchestration,
 * and transport concerns should live outside this domain model.
 *
 * @param id unique identifier of the session
 * @param userId identifier of the user participating in the session
 * @param lessonId identifier of the associated lesson
 * @param sessionState current lifecycle state of the learning session
 * @param topicMasteryLevel current mastery level of the lesson topic
 * @param userState current learning behavior of the user during the session
 * @param startTimestamp instant when the session started
 * @param endTimestamp instant when the session ended, or {@code null} while active
 */
@Builder(toBuilder = true)
public record Session(
  UUID id,
  String userId,
  String lessonId,
  SessionState sessionState,
  TopicMasteryLevel topicMasteryLevel,
  UserState userState,
  Instant startTimestamp,
  Instant endTimestamp
) {
  public Session {
    Objects.requireNonNull(id, "Session id must not be null");
    validateRequiredText(userId, "Session user id must not be blank");
    //validateRequiredText(lessonId, "Session lesson id must not be blank");
    //Objects.requireNonNull(sessionState, "Session state must not be null");
    //Objects.requireNonNull(topicMasteryLevel, "Topic mastery level must not be null");
    //Objects.requireNonNull(userState, "User state must not be null");
    Objects.requireNonNull(startTimestamp, "Session start timestamp must not be null");

    if (endTimestamp != null && endTimestamp.isBefore(startTimestamp)) {
      throw new IllegalArgumentException("Session end timestamp must not be before start timestamp");
    }
  }

  /**
   * Creates a new session in its initial learning state.
   */
  public static Session createNew(UUID id, String userId, String lessonId, Instant startTimestamp) {
    return new Session(
      id,
      userId,
      lessonId,
      SessionState.NOT_STARTED,
      TopicMasteryLevel.DISCOVERING,
      UserState.READY,
      startTimestamp,
      null
    );
  }

  /**
   * Returns a copy of this session with updated learning state.
   */
  public Session withLearningState(
    SessionState sessionState,
    TopicMasteryLevel topicMasteryLevel,
    UserState userState
  ) {
    return new Session(
      id,
      userId,
      lessonId,
      sessionState,
      topicMasteryLevel,
      userState,
      startTimestamp,
      endTimestamp
    );
  }

  /**
   * Returns a completed copy of this session.
   */
  public Session complete(Instant endTimestamp) {
    return new Session(
      id,
      userId,
      lessonId,
      SessionState.COMPLETED,
      topicMasteryLevel,
      UserState.COMPLETED,
      startTimestamp,
      endTimestamp
    );
  }

  private static void validateRequiredText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }
}
