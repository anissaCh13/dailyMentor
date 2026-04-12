package org.back.dailymentor.session.repository;

import java.util.Optional;
import org.back.dailymentor.session.entity.UserSession;

public interface SessionRepository {
  Optional<UserSession> findByUserId(String userId);

  void save(UserSession session);
}
