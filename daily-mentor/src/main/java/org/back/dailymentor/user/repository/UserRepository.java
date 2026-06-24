package org.back.dailymentor.user.repository;

import java.util.Optional;
import org.back.dailymentor.user.entity.User;

/**
 * Repository contract for user persistence.
 */
public interface UserRepository {
  /**
   * Creates a new user.
   *
   * @throws IllegalArgumentException when a user with the same id already exists
   */
  User create(User user);

  Optional<User> findById(String id);

  /**
   * Updates an existing user.
   *
   * @throws org.back.dailymentor.user.exception.UserNotFoundException when the user does not exist
   */
  User update(User user);

  /**
   * Deletes an existing user.
   *
   * @throws org.back.dailymentor.user.exception.UserNotFoundException when the user does not exist
   */
  void deleteById(String id);
}
