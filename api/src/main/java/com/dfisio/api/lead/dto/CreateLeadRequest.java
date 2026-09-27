package com.dfisio.api.lead.dto;

import com.dfisio.api.lead.LeadOrigin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLeadRequest(
        @NotBlank String name,
        @NotBlank String whatsapp,
        @NotBlank String serviceInterest,
        @NotNull LeadOrigin origin,
        @NotBlank String responsible,
        String observation
) {
}
