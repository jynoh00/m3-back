package com.example.community.common;

import lombok.Getter;

@Getter
public enum ReportStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED");

    private final String status;

    ReportStatus(String status) {
        this.status = status;
    }
}