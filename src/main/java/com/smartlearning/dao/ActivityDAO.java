package com.smartlearning.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.smartlearning.model.Activity;

/**
 * DAO du journal d'activite (table activity_logs_by_user). Repond a R7
 * Les entrees expirent apres 90 jours grace a USING TTL (donnees temporaires)
 */
public class ActivityDAO {
    private static final String TABLE = "smart_learning.activity_logs_by_user";
    private static final int TTL_SECONDS = 90 * 24 * 3600; // 7 776 000

    private final CqlSession session;
    private final PreparedStatement insertStmt;
    private final PreparedStatement selectRecentStmt;

    public ActivityDAO(CqlSession session) {
        this.session = session;
        insertStmt = session.prepare("INSERT INTO " + TABLE
                + " (user_id, activity_timestamp, activity_id, action_type, details) "
                + "VALUES (?, ?, ?, ?, ?) USING TTL " + TTL_SECONDS);
        selectRecentStmt = session.prepare("SELECT activity_timestamp, activity_id, action_type, details FROM "
                + TABLE + " WHERE user_id = ? LIMIT ?");
    }

    /** Enregistre une action (connexion, vue de cours, soumission...) */
    public Activity log(Activity activity) {
        try {
            if (activity.getTimestamp() == null) activity.setTimestamp(Instant.now());
            if (activity.getActivityId() == null) activity.setActivityId(UUID.randomUUID());
            session.execute(insertStmt.bind(activity.getUserId(), activity.getTimestamp(),
                    activity.getActivityId(), activity.getActionType(), activity.getDetails()));
            return activity;
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de l'enregistrement de l'activité", e);
        }
    }

    /** R7 : les N dernieres activites d'un utilisateur (clustering DESC sur le timestamp) */
    public List<Activity> findRecent(UUID userId, int limit) {
        try {
            List<Activity> result = new ArrayList<>();
            for (Row row : session.execute(selectRecentStmt.bind(userId, limit))) {
                Activity a = new Activity();
                a.setUserId(userId);
                a.setTimestamp(row.getInstant("activity_timestamp"));
                a.setActivityId(row.getUuid("activity_id"));
                a.setActionType(row.getString("action_type"));
                a.setDetails(row.getString("details"));
                result.add(a);
            }
            return result;
        } catch (DriverException e) {
            throw new DaoException("Erreur lors de la lecture du journal de l'utilisateur " + userId, e);
        }
    }
}
