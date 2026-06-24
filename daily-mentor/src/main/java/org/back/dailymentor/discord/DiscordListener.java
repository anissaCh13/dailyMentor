package org.back.dailymentor.discord;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.back.dailymentor.ai.entity.LessonProgress;
import org.back.dailymentor.ai.entity.UserStateV0;
import org.back.dailymentor.learning.LearningService;
import org.back.dailymentor.model.SessionStateV0;
import org.back.dailymentor.session.entity.Session;
import org.back.dailymentor.session.entity.SessionState;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.service.SessionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter {
  
  private final LearningService learningService;
  private final SessionService sessionService;

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }

    // ✅ on garde seulement les messages privés (DM)
    if (!event.isFromType(ChannelType.PRIVATE)) {
      return;
    }

    String userId = event.getAuthor().getId();
    String message = event.getMessage().getContentRaw();

    Session session = sessionService.getOrCreateSession(userId);
    
    handleUserResponse(event, session, message);
  }

  private void handleUserResponse(MessageReceivedEvent event, Session session,String message) {

    String normalized = message.toLowerCase().trim();
    
    switch (session.sessionState()) {
      case NOT_STARTED ->  handleStart(event, session,normalized);
      //case WAITING_FEEDBACK -> handleFeedback(event, session,normalized);
      default -> event.getChannel()
          .sendMessage("❓ Je ne comprends pas, réponds par 'oui' ou 'non'")
          .queue();
    }
  }

  private void handleStart(MessageReceivedEvent event, Session session,String message) {

    if (message.equalsIgnoreCase("oui")) {

      String lesson = learningService.generateAdaptiveContent(session);

      sendMessage(event, lesson);
      
      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          .state(SessionStateV0.WAITING_FEEDBACK)
          .currentTopic(extractTopic(lesson))
          .userState(UserStateV0.ENGAGED)
          .lessonProgress(LessonProgress.QUESTIONING)
          .build();
      
      sessionService.save(sessionUpdate);

      return;
    }

    if (message.equalsIgnoreCase("non")) {
      sendMessage(event, "👍 OK, à demain !");
      var sessionUpdate = session.toBuilder()
          .sessionState(SessionState.COMPLETED)
          .endTimestamp(Instant.now())
          .build();

      sessionService.updateSessionState(sessionUpdate);
      return;
    }

    sendMessage(event, "❓ Réponds par 'oui' ou 'non'");
  }

  private void handleFeedback(MessageReceivedEvent event, Session session,String message) {

    if (message.equals("oui")) {

      event.getChannel()
          .sendMessage("🎉 Parfait ! On passe à un nouveau sujet demain.")
          .queue();

      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          .state(SessionStateV0.IDLE)
          .currentTopic("")
          .userState(UserStateV0.READY_TO_PROGRESS)
          .lessonProgress(LessonProgress.COMPLETED)
          .build();

      sessionService.save(sessionUpdate);
      return;
    }

    if (message.equals("non")) {

      String explanation = learningService.generateAdaptiveContent(session);

      sendMessage(event, explanation);
      
      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          .state(SessionStateV0.WAITING_FEEDBACK)
          .currentTopic(extractTopic(explanation))
          .userState(UserStateV0.CONFUSED)
          .lessonProgress(LessonProgress.EXPLAINING)
          .build();

      sessionService.save(sessionUpdate);
      return;
    }

    sendMessage(event, "❓ Réponds par 'oui' ou 'non'");
  }
  
  private void sendMessage(MessageReceivedEvent event, String message){

    int maxLength = 2000;

    for (int i = 0; i < message.length(); i += maxLength) {
      String part = message.substring(i, Math.min(message.length(), i + maxLength));
      event.getChannel().sendMessage(part).queue();
    }
  }

  private String extractTopic(String lesson) {
    if (lesson == null || lesson.isBlank()) {
      return "unknown";
    }

    String currentLessonMarker = "\"currentLesson\":\"";
    int jsonLessonStart = lesson.indexOf(currentLessonMarker);
    if (jsonLessonStart >= 0) {
      int start = jsonLessonStart + currentLessonMarker.length();
      int end = lesson.indexOf("\"", start);

      if (end > start) {
        return lesson.substring(start, end).trim();
      }
    }

    int topicMarkerIndex = lesson.indexOf("🎯");
    if (topicMarkerIndex >= 0) {
      int questionMarkerIndex = lesson.indexOf("❓", topicMarkerIndex);

      if (questionMarkerIndex > topicMarkerIndex) {
        return lesson.substring(topicMarkerIndex, questionMarkerIndex).trim();
      }

      int lineEndIndex = lesson.indexOf("\n", topicMarkerIndex);
      if (lineEndIndex > topicMarkerIndex) {
        return lesson.substring(topicMarkerIndex, lineEndIndex).trim();
      }

      return lesson.substring(topicMarkerIndex).trim();
    }

    return "unknown";
  }
}
