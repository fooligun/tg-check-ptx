package tv.era.service.telegram;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class OneTimeCode {

    // Time-to-live of short code in minutes
    @Value("${shortcode.ttl:20}")
    private int ttlTime;

    @Value("${shortcode.length}:15")
    private int codeLength;

    private Cache<String, Object> codeCache;

    @PostConstruct
    private void init() {
         codeCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(ttlTime, TimeUnit.MINUTES).build();
    }

    public String generate(Object o) {
        String code = RandomStringUtils.randomAlphabetic(codeLength);
        codeCache.put(code, o);
        return code;
    }

    public boolean isValid(String code) {
        return Objects.nonNull(codeCache.getIfPresent(code));
    }

    public <T> T retrieve(String code) {
        var result = codeCache.getIfPresent(code);
        if (result == null) return null;
        try {
            return (T) result;
        } catch (ClassCastException e) {
            return null;
        }
    }
}
