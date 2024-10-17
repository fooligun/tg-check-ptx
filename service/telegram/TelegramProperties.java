package tv.era.service.telegram;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Getter
@Setter
@NoArgsConstructor
@ConditionalOnProperty(name = "telegram.enabled", havingValue = "true")
public class TelegramProperties {
    private boolean enabled;

    private String botName;

    private String botToken;

    private int inviteLinkTtl;
}
