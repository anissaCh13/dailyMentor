package org.back.dailymentor.lesson.entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a DailyMentor lesson.
 *
 * <p>The entity is intentionally immutable. Future lesson attributes such as description, difficulty,
 * estimated duration, prerequisites, and tags can be added without exposing mutable state.
 *
 * @param id unique identifier of the lesson
 * @param title lesson title displayed to the user
 * @param createdTimestamp instant when the lesson was created
 */
public record Lesson(String id, String title, Instant createdTimestamp) {
  public Lesson {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Lesson id must not be blank");
    }

    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Lesson title must not be blank");
    }

    Objects.requireNonNull(createdTimestamp, "Lesson created timestamp must not be null");
  }
}
