package tv.era.service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tv.era.exception.ApiError;
import tv.era.exception.AppException;
import tv.era.store.message.telegram.TelegramAccount;
import tv.era.store.message.telegram.TelegramRepository;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(TelegramBot.class)
public class TelegramService {

    private final TelegramRepository telegramRepository;
    private final TelegramBot telegramBot;
    private final TelegramUserResolver userResolver;

    public void sendMessage(Integer userId, String message) {
        telegramRepository.findByUserId(userId).map(TelegramAccount::getTelegramId).ifPresent(id -> {
            telegramBot.sendMessage(SendMessage.builder()
                    .chatId(id)
                    .text(message)
                    .build());
        });
    }

    @Transactional
    public void disconnectAccountByUser(Integer userId) {
        var user = telegramRepository.findByUserId(userId).orElseThrow(() -> new AppException(ApiError.NOT_FOUND, "user not found"));
        telegramRepository.delete(user);
        userResolver.delete(user.getTelegramId());
    }

    @Async
    public void sendBroadcastMessage(String message) {
        String template = "Message from Bot:\n\n%s".formatted(message);
        telegramRepository.findAll().stream()
                .map(user ->
                    SendMessage.builder()
                            .chatId(user.getTelegramId())
                            .text(template)
                            .build()
                ).forEach(telegramBot::sendMessage);
    }

    public boolean isConnected(Integer userId) {
        return telegramRepository.existsByUserId(userId);
    }
}
