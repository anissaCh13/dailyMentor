package org.back.dailymentor.user.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.back.dailymentor.user.entity.User;
import org.back.dailymentor.user.exception.UserNotFoundException;
import org.back.dailymentor.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JsonUserRepository implements UserRepository {
  private static final String FILE_PATH = "users.json";
  private static final TypeReference<Map<String, User>> USERS_TYPE = new TypeReference<>() {};

  private final ObjectMapper objectMapper;

  public JsonUserRepository(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public synchronized User create(User user) {
    validateUser(user);

    Map<String, User> users = readUsers();

    if (users.containsKey(user.id())) {
      throw new IllegalArgumentException("User already exists with id: " + user.id());
    }

    users.put(user.id(), user);
    writeUsers(users);

    return user;
  }

  @Override
  public synchronized Optional<User> findById(String id) {
    return Optional.ofNullable(readUsers().get(validateId(id)));
  }

  @Override
  public synchronized User update(User user) {
    validateUser(user);

    Map<String, User> users = readUsers();

    if (!users.containsKey(user.id())) {
      throw new UserNotFoundException(user.id());
    }

    users.put(user.id(), user);
    writeUsers(users);

    return user;
  }

  @Override
  public synchronized void deleteById(String id) {
    String validId = validateId(id);
    Map<String, User> users = readUsers();

    if (users.remove(validId) == null) {
      throw new UserNotFoundException(validId);
    }

    writeUsers(users);
  }

  private Map<String, User> readUsers() {
    File file = new File(FILE_PATH);

    if (!file.exists()) {
      return new LinkedHashMap<>();
    }

    try {
      Map<String, User> users = objectMapper.readValue(file, USERS_TYPE);
      return users == null ? new LinkedHashMap<>() : new LinkedHashMap<>(users);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read users from " + FILE_PATH, e);
    }
  }

  private void writeUsers(Map<String, User> users) {
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write users to " + FILE_PATH, e);
    }
  }

  private void validateUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User must not be null");
    }
  }

  private String validateId(String id) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("User id must not be blank");
    }

    return id;
  }
}
