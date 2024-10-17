package tv.era.service.telegram.command;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tv.era.service.telegram.TelegramInviteService;
import tv.era.service.telegram.TelegramProperties;
import tv.era.service.telegram.TelegramUserResolver;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(TelegramProperties.class)
public class InviteTelegramCommand implements TelegramCommand {

    private final TelegramInviteService inviteService;

    @Override
    public SendMessage apply(Update update) {
        String token = update.getMessage().getText().split(" ")[1];

        var success = inviteService.rememberUser(token, update.getMessage().getChat().getId());

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(success ? getWelcomeMessage() : getUnknownUserMessage())
                .build();
    }

    private String getWelcomeMessage() {
        return "Success! I remember you";
    }

    private String getUnknownUserMessage() {
        return "I can't remember you";
    }
}
