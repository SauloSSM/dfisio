package com.dfisio.api.lead;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeadStatusTest {

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
}
