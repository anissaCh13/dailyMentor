package org.back.dailymentor.ai.entity;

import lombok.Builder;
import org.back.dailymentor.session.entity.TopicMasteryLevel;
import org.back.dailymentor.session.entity.UserState;

@Builder(toBuilder = true)
public record PromptContext(
    UserState userState,
    TopicMasteryLevel lessonProgress,
    String currentLesson,
    String currentQuestion,
    String latestUserInput,
    String previousUserAnswer
) {}
