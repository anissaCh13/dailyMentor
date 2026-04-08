package org.back.dailymentor.discord;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class DiscordBot {
  private final JDA jda;

  public DiscordBot(@Value("${discord.token}") String token, DiscordListener discordListener)
    throws InterruptedException {
    this.jda =
      JDABuilder
        .createDefault(
          token,
          GatewayIntent.GUILD_MESSAGES,
          GatewayIntent.DIRECT_MESSAGES,
          GatewayIntent.MESSAGE_CONTENT
        )
        .addEventListeners(discordListener)
        .build()
        .awaitReady();

    log.info("Discord bot is ready");
  }

  @PreDestroy
  public void shutdown() {
    if (jda != null) {
      jda.shutdown();
    }
  }
}
