package com.acme.hrms.domain.model;

/**
 * Organizational departments across ACME Corp.
 */
public enum Department {
    ENGINEERING("Engineering"),
    PRODUCT("Product Management"),
    SALES("Sales & Revenue"),
    MARKETING("Marketing & Growth"),
    HUMAN_RESOURCES("Human Resources"),
    FINANCE("Finance & Accounting"),
    OPERATIONS("Operations & Support"),
    LEGAL("Legal & Compliance");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

