package org.back.dailymentor.user.entity;

import java.util.Objects;

/**
 * DailyMentor user profile.
 *
 * @param id unique identifier of the user
 * @param level current user skill level
 */
public record User(String id, UserLevel level) {
  public User {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("User id must not be blank");
    }

    Objects.requireNonNull(level, "User level must not be null");
  }
}
