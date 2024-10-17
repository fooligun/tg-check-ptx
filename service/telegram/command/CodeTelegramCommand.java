package tv.era.service.telegram.command;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tv.era.service.telegram.OneTimeCode;
import tv.era.service.telegram.TelegramProperties;
import tv.era.service.telegram.TelegramUserResolver;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(TelegramProperties.class)
public class CodeTelegramCommand implements TelegramCommand {

    private final TelegramUserResolver userResolver;
    private final OneTimeCode oneTimeCode;

    @Override
    public SendMessage apply(Update update) {
        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(getCodeMessage(update.getMessage().getFrom().getId()))
                .build();
    }

    private String getCodeMessage(long telegramUserId) {
        Integer userId = userResolver.resolve(telegramUserId);
        if (userId == null) {
            return "Unknown User";
        }

        return oneTimeCode.generate(userId);
    }
}
