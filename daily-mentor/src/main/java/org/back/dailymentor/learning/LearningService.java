package org.back.dailymentor.learning;

import org.springframework.stereotype.Service;

@Service
public class LearningService {

  public String generateDailyLesson() {

    return """
        🎯 Sujet du jour : Différence entre @Component, @Service et @Repository (Spring)

        ❓ Question :
        Pourquoi utilise-t-on des annotations différentes alors qu'elles font presque la même chose ?

        💡 Réponse :
        Techniquement, @Component, @Service et @Repository sont similaires :
        → elles permettent à Spring de détecter un bean automatiquement.

        MAIS leur rôle est sémantique :

        - @Component → générique
        - @Service → logique métier
        - @Repository → accès aux données

        👉 Avantage :
        - code plus lisible
        - meilleure organisation
        - permet à Spring d’ajouter des comportements spécifiques (ex: gestion des exceptions)

        🧠 À retenir :
        Toujours utiliser l’annotation la plus spécifique.

        👉 Est-ce que tu as compris ? (oui / non)
        """;
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
