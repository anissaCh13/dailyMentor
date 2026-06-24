package org.back.dailymentor.session.entity;

/**
 * Represents the lifecycle state of a learning session.
 */
public enum SessionState {
  /**
   * Session has been created but has not yet begun.
   */
  NOT_STARTED,

  /**
   * Present the lesson, explain its objectives, and motivate the user.
   */
  INTRODUCTION,

  /**
   * Evaluate the user's current knowledge before starting the lesson.
   */
  ASSESSMENT,

  /**
   * Explain concepts according to the user's current mastery level.
   */
  LEARNING,

  /**
   * Provide exercises or challenges to reinforce understanding.
   */
  PRACTICING,

  /**
   * Analyze answers and provide corrections, explanations, and encouragement.
   */
  FEEDBACK,

  /**
   * Summarize the key concepts learned during the session.
   */
  REVIEW,

  /**
   * Mark the lesson as successfully completed.
   */
  COMPLETED,

  /**
   * Persist a compressed summary used as long-term memory for future sessions.
   */
  COMPRESSED
}
