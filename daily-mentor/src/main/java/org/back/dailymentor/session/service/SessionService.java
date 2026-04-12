package org.back.dailymentor.session.service;

import lombok.RequiredArgsConstructor;
import org.back.dailymentor.model.SessionState;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {
  private final SessionRepository sessionRepository;

  public UserSession getOrCreateSession(String userId) {
    return sessionRepository
      .findByUserId(userId)
      .orElseGet(
        () -> {
          UserSession session = UserSession
            .builder()
            .userId(userId)
            .state(SessionState.WAITING_START)
            .build();

          sessionRepository.save(session);
          return session;
        }
      );
  }

  public void save(UserSession session) {
    sessionRepository.save(session);
  }
}
