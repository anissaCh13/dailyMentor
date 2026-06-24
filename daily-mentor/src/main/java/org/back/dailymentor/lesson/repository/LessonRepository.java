package org.back.dailymentor.lesson.repository;

import java.util.Optional;
import org.back.dailymentor.lesson.entity.Lesson;

/**
 * Repository contract for lesson persistence.
 */
public interface LessonRepository {
  /**
   * Creates a new lesson.
   *
   * @throws IllegalArgumentException when a lesson with the same id already exists
   */
  Lesson create(Lesson lesson);

  Optional<Lesson> findById(String id);

  /**
   * Updates an existing lesson.
   *
   * @throws org.back.dailymentor.lesson.exception.LessonNotFoundException when the lesson does not exist
   */
  Lesson update(Lesson lesson);

  /**
   * Deletes an existing lesson.
   *
   * @throws org.back.dailymentor.lesson.exception.LessonNotFoundException when the lesson does not exist
   */
  void deleteById(String id);
}
