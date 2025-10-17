package com.sw.insurance.config;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.*;
import com.launchdarkly.sdk.server.integrations.FileData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class LaunchDarklyConfig {

    @Value("${launchdarkly.sdk-key:sdk-key}")
    private String sdkKey;

    @Value("${launchdarkly.offline-mode:false}")
    private boolean offlineMode;

    @Value("${launchdarkly.flag-file:launchdarkly/ld-flags-local.json}")
    private String flagFile;

    @Bean
    @Profile("!test") // Don't use this in tests
    public LDClient ldClient() throws IOException {
        LDConfig.Builder configBuilder = new LDConfig.Builder()
                .startWait(Duration.ofSeconds(5)); // Wait up to 5 seconds for initialization

        if (offlineMode) {
            // Convert classpath resource to file path
            String filePath = new ClassPathResource(flagFile).getFile().getAbsolutePath();

            configBuilder
                    .dataSource(
                            FileData.dataSource()
                                    .filePaths(filePath)
                                    .autoUpdate(true)
                    )
                    .events(Components.noEvents());
        }

        // Create client with configuration
        return new LDClient(sdkKey, configBuilder.build());
    }
}
