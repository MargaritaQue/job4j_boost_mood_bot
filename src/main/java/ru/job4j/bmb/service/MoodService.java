package ru.job4j.bmb.service;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static java.time.LocalDateTime.*;
import static java.time.ZoneId.*;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        var content = new Content(chatId);
        List<MoodLog> moodLogs = moodLogRepository.findAll().stream()
                .filter(s -> s.getUser().getClientId() == clientId)
                .filter(s -> ofInstant(Instant.ofEpochMilli(s.getCreatedAt()), systemDefault()).isAfter(now().minusWeeks(1)))
                .toList();
        content.setText(formatMoodLogs(moodLogs, "За неделю"));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        var content = new Content(chatId);
        List<MoodLog> moodLogs = moodLogRepository.findAll().stream()
                .filter(s -> s.getUser().getClientId() == clientId)
                .filter(s -> ofInstant(Instant.ofEpochMilli(s.getCreatedAt()),
                        systemDefault()).isAfter(now().minusMonths(1)))
                .toList();
        content.setText(formatMoodLogs(moodLogs, "За месяц"));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        var content = new Content(chatId);
        StringJoiner s = new StringJoiner("\n");
        achievementRepository.findAll().stream()
                .filter(a -> a.getUser().getClientId() == clientId)
                .map(a -> a.getAward().getTitle())
                .forEach(s::add);
        content.setText(s.toString());
        return Optional.of(content);
    }
}

