package com.simplenote.backend.schedule;

import com.simplenote.backend.service.support.InteractionRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InteractionSyncScheduler {
    @Autowired
    private InteractionRedisService interactionRedisService;

    @Scheduled(fixedDelayString = "${social.sync.fixed-delay-ms}")
    public void syncInteractionData() {
        interactionRedisService.syncDirtyPostLikes();
        interactionRedisService.syncDirtyFollows();
    }
}
