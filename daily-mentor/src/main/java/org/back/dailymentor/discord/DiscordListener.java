package org.back.dailymentor.discord;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.back.dailymentor.learning.LearningService;
import org.back.dailymentor.model.SessionState;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.service.SessionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
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

    UserSession session = sessionService.getOrCreateSession(userId);
    
    handleUserResponse(event, session, message);
  }

  private void handleUserResponse(MessageReceivedEvent event, UserSession session,String message) {

    String normalized = message.toLowerCase().trim();
    
    switch (session.state()) {
      case WAITING_START ->  handleStart(event, session,normalized);
      case WAITING_FEEDBACK -> handleFeedback(event, session,normalized);
      default -> event.getChannel()
          .sendMessage("❓ Je ne comprends pas, réponds par 'oui' ou 'non'")
          .queue();
    }
  }

  private void handleStart(MessageReceivedEvent event, UserSession session,String message) {

    if (message.equals("oui")) {

      String lesson = learningService.generateDailyLesson();

      sendMessage(event, lesson);
      
      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          //.state(SessionState.WAITING_FEEDBACK)
          .state(SessionState.WAITING_START)
          .build();
      
      sessionService.save(sessionUpdate);

      return;
    }

    if (message.equals("non")) {
      sendMessage(event, "👍 OK, à demain !");
      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          .state(SessionState.IDLE)
          .build();

      sessionService.save(sessionUpdate);
      return;
    }

    sendMessage(event, "❓ Réponds par 'oui' ou 'non'");
  }

  private void handleFeedback(MessageReceivedEvent event, UserSession session,String message) {

    if (message.equals("oui")) {

      event.getChannel()
          .sendMessage("🎉 Parfait ! On passe à un nouveau sujet demain.")
          .queue();

      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          .state(SessionState.IDLE)
          .build();

      sessionService.save(sessionUpdate);
      return;
    }

    if (message.equals("non")) {

      String explanation = learningService.explainAgain();

      sendMessage(event, explanation);
      
      var sessionUpdate = UserSession.builder()
          .userId(session.userId())
          .state(SessionState.WAITING_FEEDBACK)
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
}
