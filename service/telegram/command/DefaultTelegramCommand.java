package tv.era.service.telegram.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DefaultTelegramCommand implements TelegramCommand {
    @Override
    public SendMessage apply(Update update) {
        String message = "Unknown command";
        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .build();
    }
}
