package com.smartlearning;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CassandraConnectionTest {

    private static CqlSession session;

    @BeforeAll
    public static void setUp() {
        session = CqlSession.builder()
            .withLocalDatacenter("datacenter1")
            .build();
    }

    @Test
    public void testCassandraConnection() {
        assertNotNull(session, "Session should not be null");
        assertFalse(session.isClosed(), "Session should be active and open");
    }

    @AfterAll
    public static void tearDown() {
        if (session != null) {
            session.close();
        }
    }
}