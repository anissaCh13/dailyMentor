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
      "Hello \uD83D\uDC4B ton bot DailyMentor est prêt en DM \uD83D\uDE80\n" +
      "Tu es prêt à monter en compétence chaque jour et devenir une meilleure version de toi-même ? \uD83D\uDCAA"
    );
  }
}
