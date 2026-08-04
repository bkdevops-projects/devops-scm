package com.tencent.devops.scm.sdk.gitlab.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class GitlabJsonUtil {
    private static final ScmSdkJsonFactory JSON_FACTORY = createFactory();

    private GitlabJsonUtil() {
    }

    public static ScmSdkJsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    private static ScmSdkJsonFactory createFactory() {
        ScmSdkJsonFactory factory = new ScmSdkJsonFactory();
        factory.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String value = parser.getValueAsString();
                if (value == null || value.isEmpty()) {
                    return null;
                }
                try {
                    return Date.from(Instant.parse(value));
                } catch (RuntimeException ignored) {
                    try {
                        return Date.from(OffsetDateTime.parse(value).toInstant());
                    } catch (RuntimeException dateTimeError) {
                        return Date.from(LocalDate.parse(value).atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                }
            }
        });
        factory.getObjectMapper().registerModule(module);
        return factory;
    }
}
