package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.others.BandConnectionResult;
import com.nfssoundtrack.racingsoundtracks.services.BandConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/bands")
public class BandConnectionController {

    private static final Logger logger = LoggerFactory.getLogger(BandConnectionController.class);

    private final BandConnectionService bandConnectionService;

    public BandConnectionController(BandConnectionService bandConnectionService) {
        this.bandConnectionService = bandConnectionService;
    }

    /**
     * Find the shortest connection chain between two artists via shared game soundtracks.
     * Example: GET /api/bands/connection?from=123&to=456
     */
    @GetMapping(value = "/connection", produces = MediaType.APPLICATION_JSON_VALUE)
    public String findConnection(@RequestParam("from") Long fromId,
                                 @RequestParam("to") Long toId,
                                 @RequestParam(value = "sameGroup", defaultValue = "false") boolean sameGroup)
            throws JsonProcessingException {
        logger.debug("Connection request: from={} to={} sameGroup={}", fromId, toId, sameGroup);
        BandConnectionResult result = bandConnectionService.findConnection(fromId, toId, sameGroup);
        return new ObjectMapper().writeValueAsString(result);
    }

    /**
     * Autocomplete search for artists that exist in the connection graph.
     * Returns up to 10 matches as [{id, name}] for the connections page UI.
     * Example: GET /api/bands/search?q=orbital
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchBands(@RequestParam("q") String query) throws JsonProcessingException {
        if (query == null || query.length() < 2) {
            return new ObjectMapper().writeValueAsString(Collections.emptyList());
        }
        return new ObjectMapper().writeValueAsString(bandConnectionService.searchBands(query, 10));
    }

    /**
     * Admin endpoint to rebuild the in-memory band connection graph from the database.
     * Useful after bulk data changes without restarting the app.
     * Requires ADMIN authority (see WebSecurityConfig).
     */
    @PostMapping(value = "/admin/graph/rebuild", produces = MediaType.APPLICATION_JSON_VALUE)
    public String rebuildGraph() throws JsonProcessingException {
        logger.info("Graph rebuild triggered by admin");
        bandConnectionService.rebuildGraph();
        return new ObjectMapper().writeValueAsString(
                Map.of("status", "OK",
                       "graphBuilt", bandConnectionService.isGraphBuilt(),
                       "bandCount", bandConnectionService.getBandCount()));
    }
}
