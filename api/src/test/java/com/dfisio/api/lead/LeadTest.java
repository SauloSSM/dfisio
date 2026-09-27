package com.dfisio.api.lead;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
