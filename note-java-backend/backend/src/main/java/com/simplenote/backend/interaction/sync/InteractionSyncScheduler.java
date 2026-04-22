package com.simplenote.backend.interaction.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InteractionSyncScheduler {
    @Autowired
    private InteractionSyncService interactionSyncService;

    @Scheduled(fixedDelayString = "${social.sync.fixed-delay-ms}")
    public void syncInteractionData() {
        interactionSyncService.syncDirtyPostLikes();
        interactionSyncService.syncDirtyFollows();
    }
}
