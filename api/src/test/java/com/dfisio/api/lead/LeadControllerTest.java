package com.dfisio.api.lead;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
