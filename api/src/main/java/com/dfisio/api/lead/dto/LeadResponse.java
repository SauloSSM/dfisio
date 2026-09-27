package com.dfisio.api.lead.dto;

import com.dfisio.api.lead.Lead;
import com.dfisio.api.lead.LeadOrigin;
import com.dfisio.api.lead.LeadStatus;

import java.time.Instant;

public record LeadResponse(
        Long id,
        String name,
        String whatsapp,
        String serviceInterest,
        LeadOrigin origin,
        LeadStatus status,
        String responsible,
        Instant createdAt,
        Instant lastContactAt,
        Instant nextActionAt,
        String observation
) {

    public static LeadResponse from(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getName(),
                lead.getWhatsapp(),
                lead.getServiceInterest(),
                lead.getOrigin(),
                lead.getStatus(),
                lead.getResponsible(),
                lead.getCreatedAt(),
                lead.getLastContactAt(),
                lead.getNextActionAt(),
                lead.getObservation()
        );
    }
}
