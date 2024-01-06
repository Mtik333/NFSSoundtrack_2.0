package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import com.nfssoundtrack.NFSSoundtrack_20.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(path = "/country")
public class CountryController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);
    @Autowired
    CountryRepository countryRepository;

    @GetMapping(value = "/readAll")
    public @ResponseBody String readAliases(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = countryRepository.findAll();
        return objectMapper.writeValueAsString(countries);
    }
}
