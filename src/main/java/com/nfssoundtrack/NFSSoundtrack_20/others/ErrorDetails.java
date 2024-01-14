package com.nfssoundtrack.NFSSoundtrack_20.others;

import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {
}