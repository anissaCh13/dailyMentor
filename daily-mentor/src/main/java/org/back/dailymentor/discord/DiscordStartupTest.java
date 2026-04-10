package org.back.dailymentor.discord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DiscordStartupTest implements CommandLineRunner {

  private final DiscordService discordService;
  private final String userId;

  public DiscordStartupTest(
      DiscordService discordService,
      @Value("${discord.user-id}") String userId
  ) {
    this.discordService = discordService;
    this.userId = userId;
  }

  @Override
  public void run(String... args) {
    discordService.sendPrivateMessage(
        userId,
        "Hello 👋 ton bot dailymentor fonctionne en DM 🚀"
    );
  }
}
