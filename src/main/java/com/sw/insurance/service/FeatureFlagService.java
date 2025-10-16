package com.sw.insurance.service;

import com.launchdarkly.sdk.*;
import com.launchdarkly.sdk.server.LDClient;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagService {

    private final LDClient ldClient;
    private static final LDContext DEFAULT_CONTEXT = LDContext.builder("default-context")
            .name("Default User")
            .build();

    public FeatureFlagService(LDClient ldClient) {
        this.ldClient = ldClient;
    }

    public boolean isFeatureEnabled(String featureKey, boolean defaultValue) {
        return ldClient.boolVariation(featureKey, DEFAULT_CONTEXT, defaultValue);
    }

    public boolean isFeatureEnabled(String featureKey, String userId, boolean defaultValue) {
        LDContext context = LDContext.builder(userId)
                .name("User: " + userId)
                .set("source", "backend")
                .build();
        if (ldClient.isInitialized()) {
            // Tracking your memberId lets us know you are connected.
            ldClient.track("68ef683675142709aaa21422", context);
            System.out.println("SDK successfully initialized!");
        }

        return ldClient.boolVariation(featureKey, context, defaultValue);
    }

    public void trackEvent(String eventName, String userId) {
        LDContext context = LDContext.builder(userId).build();
        ldClient.track(eventName, context);
    }
}
