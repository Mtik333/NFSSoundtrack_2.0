package com.nfssoundtrack.racingsoundtracks.others;

import java.util.Date;

/**
 * i guss i wanted to handle unexpected exception
 * @param timestamp
 * @param message
 * @param details
 */
public record ErrorDetails(Date timestamp, String message, String details) {

}