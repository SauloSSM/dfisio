package com.dfisio.api.lead;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String whatsapp;

    @Column(name = "service_interest", nullable = false, columnDefinition = "text")
    private String serviceInterest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LeadOrigin origin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LeadStatus status;

    @Column(nullable = false, columnDefinition = "text")
    private String responsible;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_contact_at")
    private Instant lastContactAt;

    @Column(name = "next_action_at")
    private Instant nextActionAt;

    @Column(columnDefinition = "text")
    private String observation;

    protected Lead() {
    }

    public Lead(
            String name,
            String whatsapp,
            String serviceInterest,
            LeadOrigin origin,
            String responsible,
            String observation
    ) {
        this.name = requireNonBlank(name, "name");
        this.whatsapp = requireNonBlank(whatsapp, "whatsapp");
        this.serviceInterest = requireNonBlank(serviceInterest, "serviceInterest");
        this.origin = Objects.requireNonNull(origin, "origin is required");
        this.status = LeadStatus.NEW;
        this.responsible = requireNonBlank(responsible, "responsible");
        this.createdAt = Instant.now();
        this.observation = observation;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value;
    }

    @PrePersist
    @PreUpdate
    void validateState() {
        Objects.requireNonNull(status, "status is required");
        if (status.requiresNextAction() && nextActionAt == null) {
            throw new IllegalStateException(status + " requires nextActionAt");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getServiceInterest() {
        return serviceInterest;
    }

    public LeadOrigin getOrigin() {
        return origin;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public String getResponsible() {
        return responsible;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastContactAt() {
        return lastContactAt;
    }

    public Instant getNextActionAt() {
        return nextActionAt;
    }

    public String getObservation() {
        return observation;
    }

    public boolean isTerminal() {
        return status.isTerminal();
    }
}
