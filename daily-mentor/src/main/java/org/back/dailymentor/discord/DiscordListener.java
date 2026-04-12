package org.back.dailymentor.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.back.dailymentor.learning.LearningService;
import org.back.dailymentor.model.SessionState;
import org.springframework.stereotype.Component;

@Component
public class DiscordListener extends ListenerAdapter {
  
  private final LearningService learningService;

  private SessionState currentState = SessionState.WAITING_START;

  public DiscordListener(LearningService learningService) {
    this.learningService = learningService;
  }


  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }

    // ✅ on garde seulement les messages privés (DM)
    if (!event.isFromType(ChannelType.PRIVATE)) {
      return;
    }


    String message = event.getMessage().getContentRaw();
    
    handleUserResponse(event, message);
  }

  private void handleUserResponse(MessageReceivedEvent event, String message) {

    String normalized = message.toLowerCase().trim();
    
    switch (currentState) {
      case WAITING_START ->  handleStart(event, normalized);
      case WAITING_FEEDBACK -> handleFeedback(event, normalized);
      default -> event.getChannel()
          .sendMessage("❓ Je ne comprends pas, réponds par 'oui' ou 'non'")
          .queue();
    }
  }

  private void handleStart(MessageReceivedEvent event, String message) {

    if (message.equals("oui")) {

      String lesson = learningService.generateDailyLesson();

      event.getChannel().sendMessage(lesson).queue();

      currentState = SessionState.WAITING_FEEDBACK;
      return;
    }

    if (message.equals("non")) {
      event.getChannel().sendMessage("👍 OK, à demain !").queue();
      currentState = SessionState.IDLE;
      return;
    }

    event.getChannel().sendMessage("❓ Réponds par 'oui' ou 'non'").queue();
  }

  private void handleFeedback(MessageReceivedEvent event, String message) {

    if (message.equals("oui")) {

      event.getChannel()
          .sendMessage("🎉 Parfait ! On passe à un nouveau sujet demain.")
          .queue();

      currentState = SessionState.IDLE;
      return;
    }

    if (message.equals("non")) {

      String explanation = learningService.explainAgain();

      event.getChannel().sendMessage(explanation).queue();

      currentState = SessionState.WAITING_FEEDBACK;
      return;
    }

    event.getChannel().sendMessage("❓ Réponds par 'oui' ou 'non'").queue();
  }
}
