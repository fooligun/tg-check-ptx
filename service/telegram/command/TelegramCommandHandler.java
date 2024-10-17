package tv.era.service.telegram.command;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tv.era.service.telegram.TelegramProperties;

import java.util.Map;

@Component
@ConditionalOnBean(TelegramProperties.class)
public class TelegramCommandHandler {

    private final Map<String, TelegramCommand> commands;

    public TelegramCommandHandler(InviteTelegramCommand inviteCommand, CodeTelegramCommand codeCommand) {
        this.commands = Map.of(
                "/start", inviteCommand,
                "/code",  codeCommand
        );
    }

    public SendMessage handle(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        return commands.getOrDefault(command, new DefaultTelegramCommand()).apply(update);
    }
}
