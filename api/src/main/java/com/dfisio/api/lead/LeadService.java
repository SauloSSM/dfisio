package com.dfisio.api.lead;

import com.dfisio.api.lead.dto.CreateLeadRequest;
import com.dfisio.api.lead.dto.LeadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Transactional
    public LeadResponse create(CreateLeadRequest request) {
        Lead lead = new Lead(
                request.name(),
                request.whatsapp(),
                request.serviceInterest(),
                request.origin(),
                request.responsible(),
                request.observation()
        );

        return LeadResponse.from(leadRepository.save(lead));
    }

    @Transactional(readOnly = true)
    public LeadResponse findById(Long id) {
        return leadRepository.findById(id)
                .map(LeadResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
    }
}
