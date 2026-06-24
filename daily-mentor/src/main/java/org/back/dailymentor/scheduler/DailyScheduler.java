package org.back.dailymentor.scheduler;

import lombok.RequiredArgsConstructor;
import org.back.dailymentor.ai.entity.LessonProgress;
import org.back.dailymentor.ai.entity.UserStateV0;
import org.back.dailymentor.discord.DiscordService;
import org.back.dailymentor.model.SessionStateV0;
import org.back.dailymentor.session.entity.Session;
import org.back.dailymentor.session.entity.UserSession;
import org.back.dailymentor.session.service.SessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyScheduler {
  private final SessionService sessionService;
  private final DiscordService discordService;

  @Value("${discord.user-id}")
  private String userId;

  @Scheduled(cron = "0 0 8 * * *", zone = "Europe/Paris")
  public void startDailySession() {
    Session session = sessionService.getOrCreateSession(userId);

    sessionService.save(
      UserSession
        .builder()
        .userId(session.userId())
        .state(SessionStateV0.WAITING_START)
        .userState(UserStateV0.ENGAGED)
        .lessonProgress(LessonProgress.STARTED)
        .build()
    );

    discordService.sendPrivateMessage(
      userId,
      "👋 Bonjour ! Tu es dispo pour une session d’apprentissage aujourd’hui ? (oui / non)"
    );
  }
}
