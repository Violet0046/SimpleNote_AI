package com.simplenote.backend.interaction.stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "social.stream")
public class InteractionStreamProperties {
    private String group = "social-interaction-sync";
    private String consumer = "backend-1";
    private Integer batchSize = 50;
    private Long blockMs = 1000L;
    private Long pollFixedDelayMs = 1000L;
    private Long reclaimFixedDelayMs = 5000L;
    private Long reclaimIdleMs = 30000L;
    private Integer reclaimBatchSize = 50;
    private Long maxDeliveries = 5L;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Long getBlockMs() {
        return blockMs;
    }

    public void setBlockMs(Long blockMs) {
        this.blockMs = blockMs;
    }

    public Long getPollFixedDelayMs() {
        return pollFixedDelayMs;
    }

    public void setPollFixedDelayMs(Long pollFixedDelayMs) {
        this.pollFixedDelayMs = pollFixedDelayMs;
    }

    public Long getReclaimFixedDelayMs() {
        return reclaimFixedDelayMs;
    }

    public void setReclaimFixedDelayMs(Long reclaimFixedDelayMs) {
        this.reclaimFixedDelayMs = reclaimFixedDelayMs;
    }

    public Long getReclaimIdleMs() {
        return reclaimIdleMs;
    }

    public void setReclaimIdleMs(Long reclaimIdleMs) {
        this.reclaimIdleMs = reclaimIdleMs;
    }

    public Integer getReclaimBatchSize() {
        return reclaimBatchSize;
    }

    public void setReclaimBatchSize(Integer reclaimBatchSize) {
        this.reclaimBatchSize = reclaimBatchSize;
    }

    public Long getMaxDeliveries() {
        return maxDeliveries;
    }

    public void setMaxDeliveries(Long maxDeliveries) {
        this.maxDeliveries = maxDeliveries;
    }
}
