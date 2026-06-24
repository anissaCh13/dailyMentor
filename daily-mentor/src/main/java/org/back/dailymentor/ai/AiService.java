package org.back.dailymentor.ai;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.back.dailymentor.session.entity.UserSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

  @Value("${openai.api-key}")
  private String apiKey;

  private final RestTemplate restTemplate = new RestTemplate();
  

  public String generateNewLesson(UserSession session) {
    String context = buildContext(session);

    String prompt = """
        Tu es un mentor expert en Java, Spring Boot et Angular.

        Contexte utilisateur :
        %s

        Ta mission :
        - Adapter ton explication selon le contexte
        - Être pédagogique et clair
        - Donner un exemple concret
        - Poser une question à la fin

        Format obligatoire :
        🎯 Sujet
        ❓ Question
        💡 Réponse
        🧠 À retenir
        👉 Question finale

        Niveau : développeur avec 7 ans d'expérience
        """.formatted(context);

    return callOpenAI(prompt);
  }

  public String explainAgain(UserSession session) {

    String prompt = """
        L'utilisateur n'a pas compris le sujet précédent.

        Sujet : %s

        Réexplique ce sujet :
        - avec une analogie simple
        - avec un exemple concret
        - plus pédagogique

        Termine par :
        "Est-ce que c'est plus clair ? (oui / non)"
        """.formatted(session.currentTopic());

    return callOpenAI(prompt);
  }
  

  private String buildContext(UserSession session) {

    StringBuilder context = new StringBuilder();

    context.append("- Etat actuel : ").append(session.state()).append("\n");

    if (session.currentTopic() != null) {
      context.append("- Dernier sujet : ").append(session.currentTopic()).append("\n");
    }

    return context.toString();
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
