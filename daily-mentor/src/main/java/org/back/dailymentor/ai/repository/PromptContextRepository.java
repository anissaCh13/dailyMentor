package org.back.dailymentor.ai.repository;

import java.util.Optional;
import org.back.dailymentor.ai.entity.PromptContext;

public interface PromptContextRepository {
  void createContext(PromptContext context);

  void updateContext(PromptContext context);

  Optional<PromptContext> getContext();
}
