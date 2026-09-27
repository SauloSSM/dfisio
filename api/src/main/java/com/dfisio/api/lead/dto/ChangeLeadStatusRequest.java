package com.dfisio.api.lead.dto;

import com.dfisio.api.lead.LeadStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ChangeLeadStatusRequest(
        @NotNull LeadStatus status,
        Instant nextActionAt
) {
}
