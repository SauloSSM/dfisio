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

    public boolean canTransitionTo(LeadStatus target) {
        if (target == null) {
            return false;
        }

        return switch (this) {
            case NEW -> target == CONTACTED || target == SCHEDULED || target == LOST;
            case CONTACTED -> target == WAITING_CLIENT
                    || target == WAITING_SCHEDULE
                    || target == SCHEDULED
                    || target == LOST;
            case WAITING_CLIENT -> target == CONTACTED
                    || target == WAITING_SCHEDULE
                    || target == SCHEDULED
                    || target == LOST;
            case WAITING_SCHEDULE -> target == CONTACTED
                    || target == WAITING_CLIENT
                    || target == SCHEDULED
                    || target == LOST;
            case SCHEDULED -> target == COMPLETED || target == RESCHEDULE_REQUIRED || target == LOST;
            case RESCHEDULE_REQUIRED -> target == SCHEDULED || target == LOST;
            case COMPLETED, LOST -> false;
        };
    }
}
