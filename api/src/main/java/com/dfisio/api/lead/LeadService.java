package com.dfisio.api.lead;

import com.dfisio.api.lead.dto.ChangeLeadStatusRequest;
import com.dfisio.api.lead.dto.CreateLeadRequest;
import com.dfisio.api.lead.dto.LeadResponse;
import com.dfisio.api.lead.dto.UpdateLeadRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        return LeadResponse.from(findLead(id));
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> findAll() {
        return leadRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(LeadResponse::from)
                .toList();
    }

    @Transactional
    public LeadResponse update(Long id, UpdateLeadRequest request) {
        Lead lead = findLead(id);
        try {
            lead.updateCommercialData(
                    request.name(),
                    request.whatsapp(),
                    request.serviceInterest(),
                    request.origin(),
                    request.responsible(),
                    request.observation()
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw badRequest(exception);
        }
        return LeadResponse.from(lead);
    }

    @Transactional
    public LeadResponse changeStatus(Long id, ChangeLeadStatusRequest request) {
        Lead lead = findLead(id);
        try {
            lead.changeStatus(request.status(), request.nextActionAt());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw badRequest(exception);
        }
        return LeadResponse.from(lead);
    }

    private Lead findLead(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
    }

    private ResponseStatusException badRequest(RuntimeException exception) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
    }
}
