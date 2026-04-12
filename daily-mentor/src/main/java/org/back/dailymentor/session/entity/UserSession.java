package org.back.dailymentor.session.entity;

import lombok.Builder;
import org.back.dailymentor.model.SessionState;

@Builder
public record UserSession (
  String userId,
  SessionState state,
  String currentTopic
){}
