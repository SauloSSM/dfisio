package com.dfisio.api.lead;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeadTest {

    @Test
    void newLeadStartsWithNewStatus() {
        Lead lead = newLead();

        assertEquals(LeadStatus.NEW, lead.getStatus());
    }

    @Test
    void newLeadHasCreatedAt() {
        Lead lead = newLead();

        assertNotNull(lead.getCreatedAt());
    }

    @Test
    void requiredTextFieldsRejectNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Lead(null, "11999999999", "Pilates", LeadOrigin.INSTAGRAM, "Ana", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("Maria", null, "Pilates", LeadOrigin.INSTAGRAM, "Ana", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("Maria", "11999999999", null, LeadOrigin.INSTAGRAM, "Ana", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("Maria", "11999999999", "Pilates", LeadOrigin.INSTAGRAM, null, null));
    }

    @Test
    void requiredTextFieldsRejectBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("", "11999999999", "Pilates", LeadOrigin.INSTAGRAM, "Ana", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("Maria", "   ", "Pilates", LeadOrigin.INSTAGRAM, "Ana", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("Maria", "11999999999", "\t", LeadOrigin.INSTAGRAM, "Ana", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Lead("Maria", "11999999999", "Pilates", LeadOrigin.INSTAGRAM, "\n", null));
    }

    @Test
    void updatesOnlyCommercialData() {
        Lead lead = newLead();
        Instant createdAt = lead.getCreatedAt();

        lead.updateCommercialData(
                "Maria Souza",
                "11888888888",
                "RPG",
                LeadOrigin.GOOGLE,
                "Carlos",
                "Contato à tarde"
        );

        assertEquals("Maria Souza", lead.getName());
        assertEquals("11888888888", lead.getWhatsapp());
        assertEquals("RPG", lead.getServiceInterest());
        assertEquals(LeadOrigin.GOOGLE, lead.getOrigin());
        assertEquals("Carlos", lead.getResponsible());
        assertEquals("Contato à tarde", lead.getObservation());
        assertEquals(LeadStatus.NEW, lead.getStatus());
        assertEquals(createdAt, lead.getCreatedAt());
        assertNull(lead.getLastContactAt());
        assertNull(lead.getNextActionAt());
    }

    @Test
    void invalidCommercialUpdateDoesNotPartiallyMutateLead() {
        Lead lead = newLead();

        assertThrows(IllegalArgumentException.class, () -> lead.updateCommercialData(
                "Changed name",
                "   ",
                "RPG",
                LeadOrigin.GOOGLE,
                "Carlos",
                null
        ));

        assertEquals("Maria", lead.getName());
        assertEquals("11999999999", lead.getWhatsapp());
    }

    @ParameterizedTest
    @MethodSource("allowedNonLossTransitions")
    void performsEveryAllowedNonLossTransition(LeadStatus source, LeadStatus target) {
        Lead lead = leadIn(source);
        Instant nextActionAt = target.requiresNextAction() ? Instant.parse("2026-10-10T12:00:00Z") : null;

        lead.changeStatus(target, nextActionAt);

        assertEquals(target, lead.getStatus());
        assertEquals(nextActionAt, lead.getNextActionAt());
    }

    @Test
    void rejectsTransitionOutsideDocumentedFlow() {
        Lead lead = newLead();

        assertThrows(IllegalStateException.class,
                () -> lead.changeStatus(LeadStatus.COMPLETED, null));

        assertEquals(LeadStatus.NEW, lead.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = LeadStatus.class, names = {
            "WAITING_CLIENT", "WAITING_SCHEDULE", "RESCHEDULE_REQUIRED"
    })
    void statusesThatRequireNextActionRejectMissingDate(LeadStatus target) {
        Lead lead = target == LeadStatus.RESCHEDULE_REQUIRED
                ? leadIn(LeadStatus.SCHEDULED)
                : leadIn(LeadStatus.CONTACTED);

        assertThrows(IllegalStateException.class, () -> lead.changeStatus(target, null));
    }

    @Test
    void leavingFollowUpStatusClearsPreviousNextAction() {
        Lead lead = leadIn(LeadStatus.WAITING_CLIENT);

        lead.changeStatus(LeadStatus.CONTACTED, Instant.parse("2027-01-01T12:00:00Z"));

        assertNull(lead.getNextActionAt());
    }

    @Test
    void statusChangePreservesCreationAndLastContactTimestamps() {
        Lead lead = newLead();
        Instant createdAt = lead.getCreatedAt();

        lead.changeStatus(LeadStatus.CONTACTED, null);

        assertEquals(createdAt, lead.getCreatedAt());
        assertNull(lead.getLastContactAt());
    }

    @Test
    void completedLeadCannotBeChangedOrReopened() {
        Lead lead = leadIn(LeadStatus.COMPLETED);

        assertThrows(IllegalStateException.class,
                () -> lead.changeStatus(LeadStatus.SCHEDULED, null));
        assertThrows(IllegalStateException.class, () -> lead.updateCommercialData(
                "Other name",
                "11888888888",
                "RPG",
                LeadOrigin.GOOGLE,
                "Carlos",
                null
        ));
    }

    @Test
    void genericStatusChangeRejectsLostUntilDedicatedFlowExists() {
        Lead lead = newLead();

        assertThrows(IllegalStateException.class,
                () -> lead.changeStatus(LeadStatus.LOST, null));

        assertEquals(LeadStatus.NEW, lead.getStatus());
    }

    private static Stream<Arguments> allowedNonLossTransitions() {
        return Stream.of(
                Arguments.of(LeadStatus.NEW, LeadStatus.CONTACTED),
                Arguments.of(LeadStatus.NEW, LeadStatus.SCHEDULED),
                Arguments.of(LeadStatus.CONTACTED, LeadStatus.WAITING_CLIENT),
                Arguments.of(LeadStatus.CONTACTED, LeadStatus.WAITING_SCHEDULE),
                Arguments.of(LeadStatus.CONTACTED, LeadStatus.SCHEDULED),
                Arguments.of(LeadStatus.WAITING_CLIENT, LeadStatus.CONTACTED),
                Arguments.of(LeadStatus.WAITING_CLIENT, LeadStatus.WAITING_SCHEDULE),
                Arguments.of(LeadStatus.WAITING_CLIENT, LeadStatus.SCHEDULED),
                Arguments.of(LeadStatus.WAITING_SCHEDULE, LeadStatus.CONTACTED),
                Arguments.of(LeadStatus.WAITING_SCHEDULE, LeadStatus.WAITING_CLIENT),
                Arguments.of(LeadStatus.WAITING_SCHEDULE, LeadStatus.SCHEDULED),
                Arguments.of(LeadStatus.SCHEDULED, LeadStatus.COMPLETED),
                Arguments.of(LeadStatus.SCHEDULED, LeadStatus.RESCHEDULE_REQUIRED),
                Arguments.of(LeadStatus.RESCHEDULE_REQUIRED, LeadStatus.SCHEDULED)
        );
    }

    private Lead leadIn(LeadStatus status) {
        Lead lead = newLead();
        Instant nextActionAt = Instant.parse("2026-10-10T12:00:00Z");

        switch (status) {
            case NEW -> {
                return lead;
            }
            case CONTACTED -> lead.changeStatus(LeadStatus.CONTACTED, null);
            case WAITING_CLIENT -> {
                lead.changeStatus(LeadStatus.CONTACTED, null);
                lead.changeStatus(LeadStatus.WAITING_CLIENT, nextActionAt);
            }
            case WAITING_SCHEDULE -> {
                lead.changeStatus(LeadStatus.CONTACTED, null);
                lead.changeStatus(LeadStatus.WAITING_SCHEDULE, nextActionAt);
            }
            case SCHEDULED -> lead.changeStatus(LeadStatus.SCHEDULED, null);
            case RESCHEDULE_REQUIRED -> {
                lead.changeStatus(LeadStatus.SCHEDULED, null);
                lead.changeStatus(LeadStatus.RESCHEDULE_REQUIRED, nextActionAt);
            }
            case COMPLETED -> {
                lead.changeStatus(LeadStatus.SCHEDULED, null);
                lead.changeStatus(LeadStatus.COMPLETED, null);
            }
            case LOST -> throw new IllegalArgumentException("LOST is created by the Block 2 loss flow");
        }
        return lead;
    }

    private Lead newLead() {
        return new Lead(
                "Maria",
                "11999999999",
                "Pilates",
                LeadOrigin.INSTAGRAM,
                "Ana",
                null
        );
    }
}
