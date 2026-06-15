package org.back.dailymentor.learning;

import lombok.RequiredArgsConstructor;
import org.back.dailymentor.ai.AiService;
import org.back.dailymentor.model.SessionState;
import org.back.dailymentor.session.entity.UserSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningService {
  
  private final AiService aiService;

  public String generateAdaptiveContent(UserSession session) {
    if (session.state() == SessionState.WAITING_START) {
      return aiService.generateNewLesson(session);
    }

    if (session.state() == SessionState.WAITING_FEEDBACK) {
      return aiService.explainAgain(session);
    }

    return aiService.generateNewLesson(session);
  }

  public String explainAgain() {

    return """
        🔁 Reprenons autrement :

        Imagine que Spring est une entreprise 👇

        - @Component → un employé générique
        - @Service → un manager (logique métier)
        - @Repository → quelqu’un qui parle à la base de données

        👉 Pourquoi c’est utile ?
        Parce que ça permet de savoir QUI fait QUOI dans ton code.

        💡 Exemple concret :

        @Service
        public class PaymentService {
            // logique métier ici
        }

        👉 Donc :
        - Component = base
        - Service = logique métier
        - Repository = accès DB

        🧠 Astuce :
        Toujours utiliser @Service pour ton métier, même si techniquement @Component marche.

        👉 Est-ce que c’est plus clair maintenant ? (oui / non)
        """;
  }

}
