package com.dfisio.api.lead;

import com.dfisio.api.lead.dto.ChangeLeadStatusRequest;
import com.dfisio.api.lead.dto.CreateLeadRequest;
import com.dfisio.api.lead.dto.LeadResponse;
import com.dfisio.api.lead.dto.UpdateLeadRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadResponse create(@Valid @RequestBody CreateLeadRequest request) {
        return leadService.create(request);
    }

    @GetMapping("/{id}")
    public LeadResponse findById(@PathVariable Long id) {
        return leadService.findById(id);
    }

    @GetMapping
    public List<LeadResponse> findAll() {
        return leadService.findAll();
    }

    @PatchMapping("/{id}")
    public LeadResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadRequest request
    ) {
        return leadService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public LeadResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeLeadStatusRequest request
    ) {
        return leadService.changeStatus(id, request);
    }
}
