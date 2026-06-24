package org.back.dailymentor.ai.service;

import java.util.Map;
import org.back.dailymentor.ai.entity.PromptContext;
import org.back.dailymentor.ai.entity.UserStateV0;
import org.back.dailymentor.ai.repository.PromptContextRepository;
import org.back.dailymentor.model.SessionStateV0;
import org.back.dailymentor.session.entity.Session;
import org.back.dailymentor.session.entity.TopicMasteryLevel;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.entity.UserState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

  @Value("${openai.api-key}")
  private String apiKey;

  private final RestTemplate restTemplate = new RestTemplate();
  private final PromptBuilder promptBuilder;
  private final PromptContextRepository promptContextRepository;

  public AiService(PromptBuilder promptBuilder, PromptContextRepository promptContextRepository) {
    this.promptBuilder = promptBuilder;
    this.promptContextRepository = promptContextRepository;
  }
  

  public String generateNewLesson() {
    PromptContext context = PromptContext.builder()
        .userState(UserState.READY)
        .lessonProgress(TopicMasteryLevel.DISCOVERING)
        .build();
    String prompt = promptBuilder.build(context);

    return callOpenAI(prompt);
  }

  public String explainAgain(Session session) {
    PromptContext context = savePromptContext(toPromptContext(session));
    String prompt = promptBuilder.build(context);

    return callOpenAI(prompt);
  }
  

  private PromptContext toPromptContext(Session session) {
    return PromptContext.builder()
        .userState(session.userState())
        .lessonProgress(session.topicMasteryLevel())
        .build();
  }

  private UserStateV0 toUserState(UserSession session) {
    SessionStateV0 state = session.state();

    if (state == null) {
      return UserStateV0.ENGAGED;
    }

    return switch (state) {
      case WAITING_START, IN_LESSON, IN_LEARNING_SESSION -> UserStateV0.ENGAGED;
      case WAITING_FEEDBACK -> UserStateV0.CONFUSED;
      case IDLE -> UserStateV0.READY_TO_PROGRESS;
    };
  }

  private PromptContext savePromptContext(PromptContext context) {
    if (promptContextRepository.getContext().isPresent()) {
      promptContextRepository.updateContext(context);
    } else {
      promptContextRepository.createContext(context);
    }

    return context;
  }
  
  private String callOpenAI(String prompt) {
    Map<String, Object> request = Map.of(
        "model", "gpt-4.1-mini",
        "input", prompt
    );
    
    var headers = new org.springframework.http.HttpHeaders();
    headers.set("Authorization", "Bearer " + apiKey);
    headers.set("Content-Type", "application/json");

    var entity = new org.springframework.http.HttpEntity<>(request, headers);

    var response = restTemplate.postForObject(
        "https://api.openai.com/v1/responses",
        entity,
        Map.class
    );

    // extraction simple (on améliorera après)
    var output = (java.util.List<Map<String, Object>>) response.get("output");
    var content = (java.util.List<Map<String, Object>>) output.get(0).get("content");

    return content.get(0).get("text").toString();
  }

}
