package tv.era.service.telegram;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import tv.era.service.telegram.command.TelegramCommandHandler;

@Component
@ConditionalOnBean(TelegramProperties.class)
@Log4j2
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramProperties properties;
    private final TelegramClient telegramClient;
    private final TelegramCommandHandler commandHandler;

    public TelegramBot(TelegramProperties properties, TelegramCommandHandler commandHandler) {
        this.properties = properties;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.commandHandler = commandHandler;
    }

    @Override
    public String getBotToken() {
        return properties.getBotToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.startsWith("/")) {
                sendMessage(commandHandler.handle(update));
            }
        }
    }


    public void sendMessage(SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("telegram sending message error", e);
        }
    }
}
