package org.back.dailymentor.session.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Optional;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.repository.SessionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JsonSessionRepository implements SessionRepository {
  private static final String FILE_PATH = "session.json";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Optional<UserSession> findByUserId(String userId) {
    try {
      File file = new File(FILE_PATH);

      if (!file.exists()) {
        return Optional.empty();
      }

      UserSession session = objectMapper.readValue(file, UserSession.class);

      return Optional.of(session);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(UserSession session) {
    try {
      objectMapper.writeValue(new File(FILE_PATH), session);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
