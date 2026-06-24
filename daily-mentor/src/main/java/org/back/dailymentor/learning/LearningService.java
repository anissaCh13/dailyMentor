package org.back.dailymentor.learning;

import lombok.RequiredArgsConstructor;
import org.back.dailymentor.ai.service.AiService;
import org.back.dailymentor.model.SessionStateV0;
import org.back.dailymentor.session.entity.Session;
import org.back.dailymentor.session.entity.SessionState;
import org.back.dailymentor.session.entity.UserSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningService {
  private final AiService aiService;

  public String generateAdaptiveContent(Session session) {
    if (session.sessionState()== SessionState.NOT_STARTED) {
      return aiService.generateNewLesson();
    }

    /*if (session.state() == SessionStateV0.WAITING_FEEDBACK) {
      return aiService.explainAgain(session);
    }*/

    return aiService.generateNewLesson();
  }
}
