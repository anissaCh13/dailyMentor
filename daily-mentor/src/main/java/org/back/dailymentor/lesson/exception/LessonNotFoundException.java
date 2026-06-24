package org.back.dailymentor.lesson.exception;

/**
 * Raised when a lesson cannot be found for the requested identifier.
 */
public class LessonNotFoundException extends RuntimeException {

  public LessonNotFoundException(String lessonId) {
    super("Lesson not found with id: " + lessonId);
  }
}
