package com.sw.insurance.config;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LaunchDarklyConfig {

    @Value("${launchdarkly.sdk-key}")
    private String sdkKey;

    @Bean
    public LDClient ldClient() {
        if (sdkKey == null || sdkKey.isEmpty()) {
            throw new IllegalStateException("LaunchDarkly SDK key is not configured");
        }

        final LDContext context = LDContext.builder("example-context-key")
                .name("Sandy")
                .build();

        LDConfig config = new LDConfig.Builder().build();

        final LDClient client = new LDClient(sdkKey, config);

        if (client.isInitialized()) {
            // Tracking your memberId lets us know you are connected.
            client.track("68ef683675142709aaa21422", context);
            System.out.println("SDK successfully initialized!");
        }

        return client;
    }
}
