package org.back.dailymentor.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordService {
  private final DiscordBot discordBot;

  public void sendPrivateMessage(String userId, String message) {
    JDA jda = discordBot.getJda();

    User user = jda.retrieveUserById(userId).complete();

    user.openPrivateChannel().flatMap(channel -> channel.sendMessage(message)).queue();
  }
}
