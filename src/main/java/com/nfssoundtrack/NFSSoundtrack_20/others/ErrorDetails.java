package com.nfssoundtrack.NFSSoundtrack_20.others;

import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {

    @Override
    public Date timestamp() {
        return timestamp;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String details() {
        return details;
    }
}