package com.nfssoundtrack.racingsoundtracks.others;

import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {

}