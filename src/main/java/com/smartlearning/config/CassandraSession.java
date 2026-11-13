package com.smartlearning.config;

import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;

public class CassandraSession {
    private static CqlSession session;

    private CassandraSession() {
        // Constructeur privé pour empêcher l'instanciation directe
    }

    public static synchronized CqlSession getSession() {
        if (session == null || session.isClosed()) {
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
                    .withLocalDatacenter("datacenter1")
                    .withKeyspace("smart_learning")
                    .build();
        }
        return session;
    }

    public static void close() {
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}