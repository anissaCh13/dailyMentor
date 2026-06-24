package org.back.dailymentor.session.entity;

import lombok.Builder;
import org.back.dailymentor.ai.entity.LessonProgress;
import org.back.dailymentor.ai.entity.UserStateV0;
import org.back.dailymentor.model.SessionStateV0;

@Builder
public record UserSession (
  String userId,
  SessionStateV0 state,
  String currentTopic,
  UserStateV0 userState,
  LessonProgress lessonProgress
) {
}
