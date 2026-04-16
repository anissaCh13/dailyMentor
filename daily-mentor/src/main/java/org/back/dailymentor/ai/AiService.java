package org.back.dailymentor.ai;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

  @Value("${openai.api-key}")
  private String apiKey;

  private final RestTemplate restTemplate = new RestTemplate();

  public String generateLesson() {


    String prompt = """
            Tu es un expert en développement Java, Spring Boot et Angular.

            Ta mission :
            - expliquer un concept avancé mais de façon pédagogique
            - donner une question
            - donner une réponse claire
            - finir par "Est-ce que tu as compris ? (oui / non)"

            Format obligatoire :
            🎯 Sujet
            ❓ Question
            💡 Réponse
            🧠 À retenir
            👉 Question finale

            Niveau : développeur avec 7 ans d'expérience
        """;

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
