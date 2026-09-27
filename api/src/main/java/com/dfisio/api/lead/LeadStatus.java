package com.dfisio.api.lead;

public enum LeadStatus {
    NEW(false, false),
    CONTACTED(false, false),
    WAITING_CLIENT(true, false),
    WAITING_SCHEDULE(true, false),
    SCHEDULED(false, false),
    RESCHEDULE_REQUIRED(true, false),
    COMPLETED(false, true),
    LOST(false, true);

    private final boolean nextActionRequired;
    private final boolean terminal;

    LeadStatus(boolean nextActionRequired, boolean terminal) {
        this.nextActionRequired = nextActionRequired;
        this.terminal = terminal;
    }

    public boolean requiresNextAction() {
        return nextActionRequired;
    }

    public boolean isTerminal() {
        return terminal;
    }
}
