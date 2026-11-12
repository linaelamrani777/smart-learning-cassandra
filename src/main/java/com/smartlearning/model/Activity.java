package com.smartlearning.model;

import java.time.Instant;
import java.util.UUID;

/** POJO : une entree du journal (table activity_logs_by_user) */
public class Activity {
    public static final String LOGIN = "LOGIN";
    public static final String VIEW_COURSE = "VIEW_COURSE";
    public static final String SUBMIT_ASSIGNMENT = "SUBMIT_ASSIGNMENT";

    private UUID userId;
    private Instant timestamp;
    private UUID activityId;
    private String actionType;
    private String details;

    public Activity() {}

    public Activity(UUID userId, String actionType, String details) {
        this.userId = userId;
        this.actionType = actionType;
        this.details = details;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public UUID getActivityId() { return activityId; }
    public void setActivityId(UUID activityId) { this.activityId = activityId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @Override
    public String toString() {
        return "Activity{user=" + userId + ", action=" + actionType + ", at=" + timestamp + "}";
    }
}
