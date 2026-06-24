package org.back.dailymentor.session.exception;

import java.util.UUID;

/**
 * Raised when a learning session cannot be found for the requested identifier.
 */
public class SessionNotFoundException extends RuntimeException {

  public SessionNotFoundException(UUID sessionId) {
    super("Session not found with id: " + sessionId);
  }
}
