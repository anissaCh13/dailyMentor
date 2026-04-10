package org.back.dailymentor.discord;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class DiscordListener extends ListenerAdapter {

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

    if (normalized.equals("oui")) {
      event.getChannel().sendMessage("🔥 Parfait ! On démarre la session du jour.").queue();
      return;
    }

    if (normalized.equals("non")) {
      event.getChannel().sendMessage("👍 Pas de souci, on remet ça demain !").queue();
      return;
    }

    event.getChannel().sendMessage("❓ Réponds par 'oui' ou 'non' 🙂").queue();
  }
}
