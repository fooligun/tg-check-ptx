package tv.era.service.telegram;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import tv.era.store.message.telegram.TelegramAccount;
import tv.era.store.message.telegram.TelegramRepository;
import tv.era.util.StringUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnBean(TelegramProperties.class)
public class TelegramInviteService {

    private final int ANONYMOUS_USER_ID = -200;

    private final Cache<String, Integer> inviteCache =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build();

    private final TelegramRepository telegramRepository;
    private final TelegramProperties telegramProperties;
    private final TelegramUserResolver userResolver;

    public TelegramInviteService(TelegramRepository telegramRepository, TelegramProperties telegramProperties, TelegramUserResolver userResolver) {
        this.telegramRepository = telegramRepository;
        this.telegramProperties = telegramProperties;
        this.userResolver = userResolver;
    }

    private String generateInviteToken(Integer userId) {
        String token = StringUtil.getRandomString(20);
        inviteCache.put(token, userId);
        return token;
    }

    private String generateAnonymousInviteToken() {
        return generateInviteToken(ANONYMOUS_USER_ID);
    }

    public String getInviteLink(Optional<Integer> userId) {
        return "https://t.me/%s?start=%s".formatted(
                telegramProperties.getBotName(),
                userId.map(this::generateInviteToken).orElseGet(this::generateAnonymousInviteToken)
        );
    }

    public boolean rememberUser(String token, Long telegramId) {
        Integer tokenUserId = inviteCache.getIfPresent(token);

        if (Objects.isNull(tokenUserId) || telegramRepository.existsById(telegramId)) {
            return false;
        }

        var isAnonymous = tokenUserId == ANONYMOUS_USER_ID;

        var account = new TelegramAccount();
        account.setTelegramId(telegramId);
        account.setUserId(isAnonymous ? null : tokenUserId);
        telegramRepository.save(account);

        if (!isAnonymous) {
            userResolver.add(telegramId, tokenUserId);
        }

        inviteCache.invalidate(token);

        return true;
    }
}
