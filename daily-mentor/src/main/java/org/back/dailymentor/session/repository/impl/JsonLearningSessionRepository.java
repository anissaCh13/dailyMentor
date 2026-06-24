package org.back.dailymentor.session.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.back.dailymentor.session.entity.Session;
import org.back.dailymentor.session.exception.SessionNotFoundException;
import org.back.dailymentor.session.repository.LearningSessionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JsonLearningSessionRepository implements LearningSessionRepository {
  private static final String FILE_PATH = "sessions.json";
  private static final TypeReference<Map<UUID, Session>> SESSIONS_TYPE = new TypeReference<>() {};

  private final ObjectMapper objectMapper;

  public JsonLearningSessionRepository(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public synchronized Session create(Session session) {
    validateSession(session);

    Map<UUID, Session> sessions = readSessions();

    if (sessions.containsKey(session.id())) {
      throw new IllegalArgumentException("Session already exists with id: " + session.id());
    }

    sessions.put(session.id(), session);
    writeSessions(sessions);

    return session;
  }

  @Override
  public synchronized Optional<Session> findById(UUID id) {
    return Optional.ofNullable(readSessions().get(validateId(id)));
  }

  @Override
  public synchronized List<Session> findByUserId(String userId) {
    String validUserId = validateRequiredText(userId, "Session user id must not be blank");

    return readSessions()
      .values()
      .stream()
      .filter(session -> session.userId().equals(validUserId))
      .toList();
  }

  @Override
  public synchronized Session update(Session session) {
    validateSession(session);

    Map<UUID, Session> sessions = readSessions();

    if (!sessions.containsKey(session.id())) {
      throw new SessionNotFoundException(session.id());
    }

    sessions.put(session.id(), session);
    writeSessions(sessions);

    return session;
  }

  @Override
  public synchronized void deleteById(UUID id) {
    UUID validId = validateId(id);
    Map<UUID, Session> sessions = readSessions();

    if (sessions.remove(validId) == null) {
      throw new SessionNotFoundException(validId);
    }

    writeSessions(sessions);
  }

  private Map<UUID, Session> readSessions() {
    File file = new File(FILE_PATH);

    if (!file.exists()) {
      return new LinkedHashMap<>();
    }

    try {
      Map<UUID, Session> sessions = objectMapper.readValue(file, SESSIONS_TYPE);
      return sessions == null ? new LinkedHashMap<>() : new LinkedHashMap<>(sessions);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read sessions from " + FILE_PATH, e);
    }
  }

  private void writeSessions(Map<UUID, Session> sessions) {
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), sessions);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write sessions to " + FILE_PATH, e);
    }
  }

  private void validateSession(Session session) {
    if (session == null) {
      throw new IllegalArgumentException("Session must not be null");
    }
  }

  private UUID validateId(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Session id must not be null");
    }

    return id;
  }

  private String validateRequiredText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }

    return value;
  }
}
