package tv.era.service.telegram.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramCommand {
    SendMessage apply(Update update);
}
