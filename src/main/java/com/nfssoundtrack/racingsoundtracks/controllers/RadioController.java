package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.radioserializers.RadioSerieSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * unused at this point but hopefully should be used once i rewrite the radio to use new endpoints
 */
@RestController
@RequestMapping(path = "/radio")
public class RadioController  {

    private final BaseControllerWithErrorHandling baseController;
    private final RadioSerieSerializer radioSerieSerializer;

    public RadioController(BaseControllerWithErrorHandling baseController, RadioSerieSerializer radioSerieSerializer) {
        this.baseController = baseController;
        this.radioSerieSerializer = radioSerieSerializer;
    }

    @GetMapping(value = "/series")
    public String getAllSeries() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Serie> seriesList = baseController.getSerieService().findAll();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Serie.class, radioSerieSerializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper.writeValueAsString(seriesList);
    }
}
