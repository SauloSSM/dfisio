package com.dfisio.api.lead;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LeadRepository leadRepository;

    @Test
    void validPostCreatesAndPersistsNewLead() throws Exception {
        String whatsapp = "5511" + UUID.randomUUID();

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Maria Silva",
                                  "whatsapp": "%s",
                                  "serviceInterest": "Pilates",
                                  "origin": "INSTAGRAM",
                                  "responsible": "Ana",
                                  "observation": "Prefere atendimento pela manhã"
                                }
                                """.formatted(whatsapp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastContactAt").doesNotExist())
                .andExpect(jsonPath("$.nextActionAt").doesNotExist());

        Lead persistedLead = leadRepository.findAll().stream()
                .filter(lead -> whatsapp.equals(lead.getWhatsapp()))
                .findFirst()
                .orElseThrow();

        assertNotNull(persistedLead.getId());
        assertNotNull(persistedLead.getCreatedAt());
        assertEquals(LeadStatus.NEW, persistedLead.getStatus());
        assertEquals("Maria Silva", persistedLead.getName());
        assertEquals("Pilates", persistedLead.getServiceInterest());
        assertEquals(LeadOrigin.INSTAGRAM, persistedLead.getOrigin());
        assertEquals("Ana", persistedLead.getResponsible());
        assertEquals("Prefere atendimento pela manhã", persistedLead.getObservation());
    }

    @Test
    void postWithBlankRequiredFieldReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Maria Silva",
                                  "whatsapp": "   ",
                                  "serviceInterest": "Pilates",
                                  "origin": "INSTAGRAM",
                                  "responsible": "Ana"
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertTrue(leadRepository.findAll().stream()
                .noneMatch(lead -> "Maria Silva".equals(lead.getName()) && "   ".equals(lead.getWhatsapp())));
    }

    @Test
    void getExistingLeadReturnsItsData() throws Exception {
        Lead lead = leadRepository.saveAndFlush(new Lead(
                "João Souza",
                "11988887777",
                "Fisioterapia",
                LeadOrigin.REFERRAL,
                "Carlos",
                null
        ));

        mockMvc.perform(get("/api/leads/{id}", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lead.getId()))
                .andExpect(jsonPath("$.name").value("João Souza"))
                .andExpect(jsonPath("$.whatsapp").value("11988887777"))
                .andExpect(jsonPath("$.serviceInterest").value("Fisioterapia"))
                .andExpect(jsonPath("$.origin").value("REFERRAL"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.responsible").value("Carlos"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastContactAt").doesNotExist())
                .andExpect(jsonPath("$.nextActionAt").doesNotExist())
                .andExpect(jsonPath("$.observation").doesNotExist());
    }

    @Test
    void getMissingLeadReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/leads/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReturnsEmptyListWhenThereAreNoLeads() throws Exception {
        leadRepository.deleteAll();
        leadRepository.flush();

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllReturnsMostRecentLeadsFirst() throws Exception {
        leadRepository.deleteAll();
        Lead older = leadRepository.saveAndFlush(newLead("Older lead", "11911111111"));
        Thread.sleep(2);
        Lead newer = leadRepository.saveAndFlush(newLead("Newer lead", "11922222222"));

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(newer.getId()))
                .andExpect(jsonPath("$[0].name").value("Newer lead"))
                .andExpect(jsonPath("$[1].id").value(older.getId()))
                .andExpect(jsonPath("$[1].name").value("Older lead"));
    }

    @Test
    void patchUpdatesCommercialDataWithoutChangingProtectedFields() throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead("Original name", "11933333333"));
        Instant createdAt = lead.getCreatedAt();

        mockMvc.perform(patch("/api/leads/{id}", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 999999,
                                  "name": "Updated name",
                                  "whatsapp": "11944444444",
                                  "serviceInterest": "RPG",
                                  "origin": "GOOGLE",
                                  "status": "COMPLETED",
                                  "responsible": "Carlos",
                                  "createdAt": "2000-01-01T00:00:00Z",
                                  "lastContactAt": "2000-01-02T00:00:00Z",
                                  "nextActionAt": "2000-01-03T00:00:00Z",
                                  "observation": "Updated observation"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lead.getId()))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.whatsapp").value("11944444444"))
                .andExpect(jsonPath("$.serviceInterest").value("RPG"))
                .andExpect(jsonPath("$.origin").value("GOOGLE"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.responsible").value("Carlos"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$.lastContactAt").doesNotExist())
                .andExpect(jsonPath("$.nextActionAt").doesNotExist())
                .andExpect(jsonPath("$.observation").value("Updated observation"));

        Lead updated = leadRepository.findById(lead.getId()).orElseThrow();
        assertEquals(LeadStatus.NEW, updated.getStatus());
        assertEquals(createdAt, updated.getCreatedAt());
        assertNull(updated.getLastContactAt());
        assertNull(updated.getNextActionAt());
    }

    @Test
    void patchWithInvalidCommercialDataReturnsBadRequest() throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead("Original name", "11955555555"));

        mockMvc.perform(patch("/api/leads/{id}", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Changed name",
                                  "whatsapp": "   ",
                                  "serviceInterest": "RPG",
                                  "origin": "GOOGLE",
                                  "responsible": "Carlos"
                                }
                                """))
                .andExpect(status().isBadRequest());

        Lead unchanged = leadRepository.findById(lead.getId()).orElseThrow();
        assertEquals("Original name", unchanged.getName());
        assertEquals("11955555555", unchanged.getWhatsapp());
    }

    @Test
    void patchMissingLeadReturnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/leads/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest()))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchCannotEditCompletedLead() throws Exception {
        Lead lead = newLead("Completed lead", "11966666666");
        lead.changeStatus(LeadStatus.SCHEDULED, null);
        lead.changeStatus(LeadStatus.COMPLETED, null);
        leadRepository.saveAndFlush(lead);

        mockMvc.perform(patch("/api/leads/{id}", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void statusPatchPerformsAllowedTransitionAndPreservesTimestamps() throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead("Contact lead", "11977777777"));
        Instant createdAt = lead.getCreatedAt();

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "CONTACTED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONTACTED"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$.lastContactAt").doesNotExist())
                .andExpect(jsonPath("$.nextActionAt").doesNotExist());

        Lead updated = leadRepository.findById(lead.getId()).orElseThrow();
        assertEquals(createdAt, updated.getCreatedAt());
        assertNull(updated.getLastContactAt());
    }

    @ParameterizedTest
    @EnumSource(value = LeadStatus.class, names = {
            "WAITING_CLIENT", "WAITING_SCHEDULE", "RESCHEDULE_REQUIRED"
    })
    void statusPatchRejectsInvalidFollowUpTransitionFromNew(LeadStatus target) throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead(
                "Invalid transition to " + target,
                "5511" + UUID.randomUUID()
        ));

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "%s",
                                  "nextActionAt": "2026-10-10T12:00:00Z"
                                }
                                """.formatted(target)))
                .andExpect(status().isBadRequest());

        assertEquals(LeadStatus.NEW, leadRepository.findById(lead.getId()).orElseThrow().getStatus());
    }

    @Test
    void statusPatchAllowsNewToContactedThenWaitingClientWithNextAction() throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead("Follow-up lead", "5511" + UUID.randomUUID()));
        Instant nextActionAt = Instant.parse("2026-10-10T12:00:00Z");

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "CONTACTED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONTACTED"));

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "WAITING_CLIENT",
                                  "nextActionAt": "%s"
                                }
                                """.formatted(nextActionAt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_CLIENT"))
                .andExpect(jsonPath("$.nextActionAt").value(nextActionAt.toString()));

        Lead updated = leadRepository.findById(lead.getId()).orElseThrow();
        assertEquals(LeadStatus.WAITING_CLIENT, updated.getStatus());
        assertEquals(nextActionAt, updated.getNextActionAt());
    }

    @Test
    void statusPatchRejectsTransitionOutsideFlow() throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead("Invalid transition", "11988888888"));

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "COMPLETED"}
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(LeadStatus.NEW, leadRepository.findById(lead.getId()).orElseThrow().getStatus());
    }

    @Test
    void statusPatchRejectsFollowUpStatusesWithoutNextActionAt() throws Exception {
        Lead contactedForClient = leadIn(LeadStatus.CONTACTED, "11910101010");
        Lead contactedForSchedule = leadIn(LeadStatus.CONTACTED, "11920202020");
        Lead scheduled = leadIn(LeadStatus.SCHEDULED, "11930303030");

        assertMissingNextActionRejected(contactedForClient, LeadStatus.WAITING_CLIENT);
        assertMissingNextActionRejected(contactedForSchedule, LeadStatus.WAITING_SCHEDULE);
        assertMissingNextActionRejected(scheduled, LeadStatus.RESCHEDULE_REQUIRED);
    }

    @Test
    void statusPatchClearsPreviousNextActionWhenLeavingFollowUpState() throws Exception {
        Lead lead = leadIn(LeadStatus.WAITING_CLIENT, "11940404040");

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CONTACTED",
                                  "nextActionAt": "2027-01-01T12:00:00Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONTACTED"))
                .andExpect(jsonPath("$.nextActionAt").doesNotExist());
    }

    @Test
    void completedLeadCannotBeReopened() throws Exception {
        Lead lead = leadIn(LeadStatus.COMPLETED, "11950505050");

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "SCHEDULED"}
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(LeadStatus.COMPLETED, leadRepository.findById(lead.getId()).orElseThrow().getStatus());
    }

    @Test
    void genericStatusPatchRejectsLost() throws Exception {
        Lead lead = leadRepository.saveAndFlush(newLead("Potential loss", "11960606060"));

        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "LOST"}
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(LeadStatus.NEW, leadRepository.findById(lead.getId()).orElseThrow().getStatus());
    }

    private void assertMissingNextActionRejected(Lead lead, LeadStatus target) throws Exception {
        mockMvc.perform(patch("/api/leads/{id}/status", lead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "%s"}
                                """.formatted(target)))
                .andExpect(status().isBadRequest());
    }

    private Lead leadIn(LeadStatus status, String whatsapp) {
        Lead lead = newLead(status + " lead", whatsapp);
        Instant nextActionAt = Instant.parse("2026-10-10T12:00:00Z");

        switch (status) {
            case CONTACTED -> lead.changeStatus(LeadStatus.CONTACTED, null);
            case WAITING_CLIENT -> {
                lead.changeStatus(LeadStatus.CONTACTED, null);
                lead.changeStatus(LeadStatus.WAITING_CLIENT, nextActionAt);
            }
            case SCHEDULED -> lead.changeStatus(LeadStatus.SCHEDULED, null);
            case COMPLETED -> {
                lead.changeStatus(LeadStatus.SCHEDULED, null);
                lead.changeStatus(LeadStatus.COMPLETED, null);
            }
            default -> throw new IllegalArgumentException("Unsupported test status: " + status);
        }

        return leadRepository.saveAndFlush(lead);
    }

    private Lead newLead(String name, String whatsapp) {
        return new Lead(
                name,
                whatsapp,
                "Pilates",
                LeadOrigin.INSTAGRAM,
                "Ana",
                null
        );
    }

    private String validUpdateRequest() {
        return """
                {
                  "name": "Updated name",
                  "whatsapp": "11999999999",
                  "serviceInterest": "RPG",
                  "origin": "GOOGLE",
                  "responsible": "Carlos",
                  "observation": "Updated observation"
                }
                """;
    }
}
