package org.back.dailymentor.session.entity;

/**
 * Represents the user's current behavior during a learning session.
 */
public enum UserState {
  READY,
  LEARNING,
  THINKING,
  ANSWERING,
  STRUGGLING,
  CONFUSED,
  UNDERSTOOD,
  PRACTICING,
  COMPLETED
}
