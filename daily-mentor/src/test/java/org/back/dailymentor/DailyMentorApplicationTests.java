package org.back.dailymentor;

import org.back.dailymentor.discord.DiscordBot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class DailyMentorApplicationTests {
  
  @MockitoBean
  private DiscordBot discordBot;

  @Test
  void contextLoads() {}
}
