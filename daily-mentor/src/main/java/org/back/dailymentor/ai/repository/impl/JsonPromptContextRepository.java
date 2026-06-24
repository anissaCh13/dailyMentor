package org.back.dailymentor.ai.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Optional;
import org.back.dailymentor.ai.entity.PromptContext;
import org.back.dailymentor.ai.repository.PromptContextRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JsonPromptContextRepository implements PromptContextRepository {
  private static final String FILE_PATH = "prompt-context.json";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void createContext(PromptContext context) {
    save(context);
  }

  @Override
  public void updateContext(PromptContext context) {
    save(context);
  }

  @Override
  public Optional<PromptContext> getContext() {
    try {
      File file = new File(FILE_PATH);

      if (!file.exists()) {
        return Optional.empty();
      }

      return Optional.of(objectMapper.readValue(file, PromptContext.class));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void save(PromptContext context) {
    try {
      objectMapper.writeValue(new File(FILE_PATH), context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
