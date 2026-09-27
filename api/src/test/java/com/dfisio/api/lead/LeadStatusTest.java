package com.dfisio.api.lead;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadStatusTest {

    @Test
    void matchesTheDocumentedTransitionMatrixExactly() {
        Map<LeadStatus, EnumSet<LeadStatus>> expectedTransitions = Map.of(
                LeadStatus.NEW, EnumSet.of(LeadStatus.CONTACTED, LeadStatus.SCHEDULED, LeadStatus.LOST),
                LeadStatus.CONTACTED, EnumSet.of(
                        LeadStatus.WAITING_CLIENT,
                        LeadStatus.WAITING_SCHEDULE,
                        LeadStatus.SCHEDULED,
                        LeadStatus.LOST
                ),
                LeadStatus.WAITING_CLIENT, EnumSet.of(
                        LeadStatus.CONTACTED,
                        LeadStatus.WAITING_SCHEDULE,
                        LeadStatus.SCHEDULED,
                        LeadStatus.LOST
                ),
                LeadStatus.WAITING_SCHEDULE, EnumSet.of(
                        LeadStatus.CONTACTED,
                        LeadStatus.WAITING_CLIENT,
                        LeadStatus.SCHEDULED,
                        LeadStatus.LOST
                ),
                LeadStatus.SCHEDULED, EnumSet.of(
                        LeadStatus.COMPLETED,
                        LeadStatus.RESCHEDULE_REQUIRED,
                        LeadStatus.LOST
                ),
                LeadStatus.RESCHEDULE_REQUIRED, EnumSet.of(LeadStatus.SCHEDULED, LeadStatus.LOST),
                LeadStatus.COMPLETED, EnumSet.noneOf(LeadStatus.class),
                LeadStatus.LOST, EnumSet.noneOf(LeadStatus.class)
        );

        for (LeadStatus source : LeadStatus.values()) {
            for (LeadStatus target : LeadStatus.values()) {
                assertEquals(
                        expectedTransitions.get(source).contains(target),
                        source.canTransitionTo(target),
                        () -> "Unexpected transition rule for " + source + " -> " + target
                );
            }
        }
    }

    @Test
    void identifiesStatusesThatRequireNextAction() {
        EnumSet<LeadStatus> statuses = EnumSet.noneOf(LeadStatus.class);

        for (LeadStatus status : LeadStatus.values()) {
            if (status.requiresNextAction()) {
                statuses.add(status);
            }
        }

        assertEquals(
                EnumSet.of(
                        LeadStatus.WAITING_CLIENT,
                        LeadStatus.WAITING_SCHEDULE,
                        LeadStatus.RESCHEDULE_REQUIRED
                ),
                statuses
        );
    }

    @Test
    void identifiesTerminalStatuses() {
        EnumSet<LeadStatus> statuses = EnumSet.noneOf(LeadStatus.class);

        for (LeadStatus status : LeadStatus.values()) {
            if (status.isTerminal()) {
                statuses.add(status);
            }
        }

        assertEquals(EnumSet.of(LeadStatus.COMPLETED, LeadStatus.LOST), statuses);
    }

    @Test
    void preservesDocumentedTransitionsToLostAsConceptuallyValid() {
        assertTrue(LeadStatus.NEW.canTransitionTo(LeadStatus.LOST));
        assertTrue(LeadStatus.CONTACTED.canTransitionTo(LeadStatus.LOST));
        assertTrue(LeadStatus.WAITING_CLIENT.canTransitionTo(LeadStatus.LOST));
        assertTrue(LeadStatus.WAITING_SCHEDULE.canTransitionTo(LeadStatus.LOST));
        assertTrue(LeadStatus.SCHEDULED.canTransitionTo(LeadStatus.LOST));
        assertTrue(LeadStatus.RESCHEDULE_REQUIRED.canTransitionTo(LeadStatus.LOST));
        assertFalse(LeadStatus.COMPLETED.canTransitionTo(LeadStatus.LOST));
        assertFalse(LeadStatus.LOST.canTransitionTo(LeadStatus.LOST));
    }
}
