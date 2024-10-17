package tv.era.service.telegram;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import tv.era.store.message.telegram.TelegramAccount;
import tv.era.store.message.telegram.TelegramRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(TelegramProperties.class)
public class TelegramUserResolver {

    private final TelegramRepository telegramRepository;
    private Map<Long, Integer> telegramToUserMap;

    @PostConstruct
    private void init() {
        telegramToUserMap = telegramRepository.findAll().stream()
                        .filter(telegramAccount -> telegramAccount.getUserId() != null)
                        .collect(Collectors.toConcurrentMap(TelegramAccount::getTelegramId, TelegramAccount::getUserId));
    }

    // Return system user id from telegram id
    public Integer resolve(long telegramUserId) {
        return telegramToUserMap.get(telegramUserId);
    }

    public void delete(Long telegramId) {
        telegramToUserMap.remove(telegramId);
    }

    public void add(Long telegramId, Integer userId) {
        telegramToUserMap.put(telegramId, userId);
    }
}
