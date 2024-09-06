package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.radioserializers.RadioSerieSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * unused at this point but hopefully should be used once i rewrite the radio to use new endpoints
 */
@Controller
@RequestMapping(path = "/radio")
public class RadioController extends BaseControllerWithErrorHandling {

    @Autowired
    RadioSerieSerializer radioSerieSerializer;

    @GetMapping(value = "/series")
    public @ResponseBody
    String getAllSeries() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Serie> seriesList = serieService.findAll();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Serie.class, radioSerieSerializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper.writeValueAsString(seriesList);
    }
}
