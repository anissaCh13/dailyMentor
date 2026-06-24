package org.back.dailymentor.ai.service;

import org.back.dailymentor.ai.entity.PromptContext;
import org.back.dailymentor.session.entity.TopicMasteryLevel;
import org.back.dailymentor.session.entity.UserState;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

  public String build(PromptContext context) {
    PromptContext safeContext = context == null ? emptyContext() : context;
    StringBuilder prompt = new StringBuilder();

    appendRole(prompt);
    appendOutputContract(prompt);
    appendLearningRules(prompt);
    appendContext(prompt, safeContext);
    appendPedagogicalStrategy(prompt, safeContext.userState());
    appendMasteryInstructions(prompt, safeContext.lessonProgress());
    appendResponseStructure(prompt, safeContext.userState());

    return prompt.toString();
  }

  private PromptContext emptyContext() {
    return new PromptContext(null, null, null, null, null, null);
  }

  private void appendRole(StringBuilder prompt) {
    prompt.append("""
        Tu es DailyMentor, un mentor expert en Java, Spring Boot et Angular.

        Mission :
        - Produire une lecon claire, simple, progressive et structuree.
        - Adapter l'explication au contexte utilisateur.
        - Utiliser des exemples concrets, proches d'un projet reel.
        - Faire progresser l'utilisateur sans aller trop vite.
        """);
  }

  private void appendOutputContract(StringBuilder prompt) {
    prompt.append("""

        Contrat de sortie obligatoire :
        - La premiere ligne doit toujours etre un JSON valide, sur une seule ligne, sans texte avant.
        - Le JSON doit contenir exactement ces champs :
          {
            "currentLesson": "titre ou sujet de la lecon en cours",
            "currentQuestion": null,
            "userState": "READY | LEARNING | THINKING | ANSWERING | STRUGGLING | CONFUSED | UNDERSTOOD | PRACTICING | COMPLETED"
          }
        - Utilise uniquement ces valeurs pour userState :
          READY, LEARNING, THINKING, ANSWERING, STRUGGLING, CONFUSED, UNDERSTOOD, PRACTICING, COMPLETED.
        - currentLesson doit refleter le sujet reellement explique dans la reponse.
        - currentQuestion doit rester null tant qu'aucune vraie question n'est posee.
        - Si tu poses une vraie question, currentQuestion doit contenir exactement cette question.
        - Apres le JSON, ajoute une ligne vide puis le contenu pedagogique.

        Exemple valide pour le debut d'une lecon :
        {"currentLesson":"Injection de dependances Spring","currentQuestion":null,"userState":"LEARNING"}
        """);
  }

  private void appendLearningRules(StringBuilder prompt) {
    prompt.append("""

        Regles pedagogiques prioritaires :
        - Au debut, commence par enseigner. Ne pose pas de question technique et ne donne pas d'exercice.
        - Explique d'abord le sujet avec une progression logique : definition, utilite, exemple, point important.
        - Ne commence les questions que si l'utilisateur indique explicitement qu'il a compris ou qu'il est pret.
        - Termine une lecon initiale par : "Si tu as compris, dis-moi que tu es pret et je te poserai quelques questions."
        - Si l'utilisateur est confus ou bloque, simplifie avec une analogie et un exemple plus simple.
        - Ne marque userState COMPLETED que si la session ou la notion est reellement terminee.
        """);
  }

  private void appendContext(StringBuilder prompt, PromptContext context) {
    prompt.append("\nContexte utilisateur :\n");
    appendIfPresent(prompt, "Etat actuel", context.userState());
    appendIfPresent(prompt, "Lecon en cours", context.currentLesson());
    appendIfPresent(prompt, "Question en cours", context.currentQuestion());
    appendIfPresent(prompt, "Derniere saisie utilisateur", context.latestUserInput());
    appendIfPresent(prompt, "Reponse precedente utilisateur", context.previousUserAnswer());
    appendIfPresent(prompt, "Progression de la lecon", context.lessonProgress());
  }

  private void appendPedagogicalStrategy(StringBuilder prompt, UserState userState) {
    prompt.append("\nStrategie selon l'etat utilisateur :\n");

    if (userState == null) {
      prompt.append("""
          - Considerer que l'utilisateur debute ou reprend une session.
          - Produire une lecon introductive claire.
          - Ne pas poser de question technique.
          - Finir par l'invitation douce demandant si l'utilisateur est pret.
          """);
      return;
    }

    switch (userState) {
      case READY -> prompt.append("""
          - Produire une vraie lecon pedagogique.
          - Introduire le sujet simplement.
          - Donner un exemple concret.
          - Ne pas poser d'exercice ni de question technique.
          - Mettre currentQuestion a null.
          - Finir par l'invitation : "Si tu as compris, dis-moi que tu es pret et je te poserai quelques questions."
          """);
      case LEARNING -> prompt.append("""
          - Continuer la lecon progressivement.
          - Clarifier le concept avec un exemple concret.
          - Ne pas evaluer l'utilisateur tout de suite.
          - Mettre currentQuestion a null.
          - Finir par l'invitation douce si la lecon est suffisante.
          """);
      case THINKING -> prompt.append("""
          - Laisser l'utilisateur raisonner.
          - Donner un indice court si une question est deja en cours.
          - Ne pas changer brutalement de sujet.
          """);
      case ANSWERING -> prompt.append("""
          - Corriger la reponse de l'utilisateur.
          - Commencer par ce qui est correct.
          - Corriger les erreurs avec une justification courte.
          - Donner la bonne reponse seulement apres l'analyse.
          """);
      case STRUGGLING -> prompt.append("""
          - Reduire la difficulte.
          - Decouper l'explication en petites etapes.
          - Utiliser une analogie simple.
          - Donner un exemple plus facile.
          - Ne pas ajouter de nouvel exercice.
          """);
      case CONFUSED -> prompt.append("""
          - Reformuler sans repeter exactement la meme phrase.
          - Utiliser une analogie simple.
          - Donner un exemple minimal.
          - Finir par l'invitation douce, pas par un quiz.
          """);
      case UNDERSTOOD -> prompt.append("""
          - L'utilisateur indique qu'il a compris ou qu'il est pret.
          - Tu peux maintenant commencer une courte phase de questions.
          - Pose une seule question claire.
          - Renseigner currentQuestion avec cette question.
          - Mettre userState a THINKING ou ANSWERING selon le contexte.
          """);
      case PRACTICING -> prompt.append("""
          - Proposer un exercice pratique cible.
          - L'exercice doit etre court et directement lie a la lecon.
          - Ne pas donner la solution complete avant la tentative utilisateur.
          - Renseigner currentQuestion avec l'enonce de l'exercice.
          """);
      case COMPLETED -> prompt.append("""
          - Resumer les points essentiels.
          - Ne pas ouvrir une nouvelle lecon dans la meme reponse.
          - Indiquer la prochaine notion pertinente de facon courte.
          """);
    }
  }

  private void appendMasteryInstructions(StringBuilder prompt, TopicMasteryLevel masteryLevel) {
    if (masteryLevel == null) {
      return;
    }

    prompt.append("\nAdaptation selon la maitrise du sujet :\n");

    switch (masteryLevel) {
      case DISCOVERING -> prompt.append("""
          - Utiliser des mots simples.
          - Expliquer pourquoi le sujet existe avant d'expliquer comment l'utiliser.
          - Eviter les details avances.
          """);
      case LEARNING -> prompt.append("""
          - Expliquer les concepts principaux.
          - Ajouter un exemple concret et court.
          - Faire explicitement le lien entre definition et usage.
          """);
      case PRACTICING -> prompt.append("""
          - Si l'utilisateur a demande a pratiquer, proposer un exercice.
          - Sinon, terminer la lecon par l'invitation a se declarer pret.
          """);
      case PROFICIENT -> prompt.append("""
          - Aller plus vite sur les bases.
          - Ajouter une bonne pratique ou un piege courant.
          - Rester concret.
          """);
      case MASTERED -> prompt.append("""
          - Relier le sujet a des decisions d'architecture ou de production.
          - Proposer une nuance avancee seulement si elle aide la lecon.
          """);
    }
  }

  private void appendResponseStructure(StringBuilder prompt, UserState userState) {
    prompt.append("""

        Structure recommandee apres le JSON :
        🎯 Sujet
        💡 Explication simple
        🔧 Exemple concret
        🧠 A retenir
        """);

    if (userState == UserState.UNDERSTOOD || userState == UserState.PRACTICING) {
      prompt.append("""
          ❓ Question
          """);
      return;
    }

    if (userState == UserState.ANSWERING) {
      prompt.append("""
          ✅ Correction
          🧠 A retenir
          """);
      return;
    }

    prompt.append("""
        👉 Fin obligatoire :
        Si tu as compris, dis-moi que tu es pret et je te poserai quelques questions.
        """);
  }

  private void appendIfPresent(StringBuilder prompt, String label, String value) {
    if (value != null && !value.isBlank()) {
      prompt.append("- ").append(label).append(" : ").append(value).append("\n");
    }
  }

  private void appendIfPresent(StringBuilder prompt, String label, Enum<?> value) {
    if (value != null) {
      prompt.append("- ").append(label).append(" : ").append(value.name()).append("\n");
    }
  }
}
