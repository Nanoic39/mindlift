package cn.dcstd.web.mindlift3.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author NaNo1c
 */
@Configuration
@Data
public class GlobalConfiguration {

    @Value("${custom.token.token-expire-time}")
    private int tokenExpireTime;

    @Value("${custom.url.base-file-url}")
    private String baseFileURL;

    @Value("${custom.url.star-api}")
    private String starApi;

    @Value("${custom.url.bert-api}")
    private String bertApi;

    @Value("${custom.key.star-app-id}")
    private String starAppId;

    @Value("${custom.key.star-secret-key}")
    private String starSecretKey;

    @Value("${custom.key.star-from}")
    private String starFrom;

    @Value("${custom.key.star-access-token}")
    private String starAccessToken;

    @Value("${custom.key.ALIBABA_CLOUD_ACCESS_KEY_ID}")
    private String ALIBABA_CLOUD_ACCESS_KEY_ID;

    @Value("${custom.key.ALIBABA_CLOUD_ACCESS_KEY_SECRET}")
    private String ALIBABA_CLOUD_ACCESS_KEY_SECRET;
}
