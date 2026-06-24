package org.back.dailymentor.lesson.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.back.dailymentor.lesson.entity.Lesson;
import org.back.dailymentor.lesson.exception.LessonNotFoundException;
import org.back.dailymentor.lesson.repository.LessonRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JsonLessonRepository implements LessonRepository {
  private static final String FILE_PATH = "lessons.json";
  private static final TypeReference<Map<String, Lesson>> LESSONS_TYPE = new TypeReference<>() {};

  private final ObjectMapper objectMapper;

  public JsonLessonRepository(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public synchronized Lesson create(Lesson lesson) {
    validateLesson(lesson);

    Map<String, Lesson> lessons = readLessons();

    if (lessons.containsKey(lesson.id())) {
      throw new IllegalArgumentException("Lesson already exists with id: " + lesson.id());
    }

    lessons.put(lesson.id(), lesson);
    writeLessons(lessons);

    return lesson;
  }

  @Override
  public synchronized Optional<Lesson> findById(String id) {
    return Optional.ofNullable(readLessons().get(validateId(id)));
  }

  @Override
  public synchronized Lesson update(Lesson lesson) {
    validateLesson(lesson);

    Map<String, Lesson> lessons = readLessons();

    if (!lessons.containsKey(lesson.id())) {
      throw new LessonNotFoundException(lesson.id());
    }

    lessons.put(lesson.id(), lesson);
    writeLessons(lessons);

    return lesson;
  }

  @Override
  public synchronized void deleteById(String id) {
    String validId = validateId(id);
    Map<String, Lesson> lessons = readLessons();

    if (lessons.remove(validId) == null) {
      throw new LessonNotFoundException(validId);
    }

    writeLessons(lessons);
  }

  private Map<String, Lesson> readLessons() {
    File file = new File(FILE_PATH);

    if (!file.exists()) {
      return new LinkedHashMap<>();
    }

    try {
      Map<String, Lesson> lessons = objectMapper.readValue(file, LESSONS_TYPE);
      return lessons == null ? new LinkedHashMap<>() : new LinkedHashMap<>(lessons);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read lessons from " + FILE_PATH, e);
    }
  }

  private void writeLessons(Map<String, Lesson> lessons) {
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), lessons);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write lessons to " + FILE_PATH, e);
    }
  }

  private void validateLesson(Lesson lesson) {
    if (lesson == null) {
      throw new IllegalArgumentException("Lesson must not be null");
    }
  }

  private String validateId(String id) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Lesson id must not be blank");
    }

    return id;
  }
}
