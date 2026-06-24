package org.back.dailymentor.user.exception;

/**
 * Raised when a user cannot be found for the requested identifier.
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String userId) {
    super("User not found with id: " + userId);
  }
}
