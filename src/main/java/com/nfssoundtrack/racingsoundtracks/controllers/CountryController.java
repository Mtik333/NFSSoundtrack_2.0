package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.CountrySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(path = "/country")
public class CountryController extends BaseControllerWithErrorHandling {

    @Autowired
    CountrySerializer countrySerializer;

    @GetMapping(value = "/readAll")
    public @ResponseBody
    String readCountries() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = countryService.findAll();
        return objectMapper.writeValueAsString(countries);
    }

    @GetMapping(value = "/read/{countryId}")
    public @ResponseBody
    String readCountry(@PathVariable("countryId") int countryId) throws JsonProcessingException, ResourceNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        Country country = countryService.findById(countryId).orElseThrow(() -> new ResourceNotFoundException("no country found with id " + countryId));
        return objectMapper.writeValueAsString(country);
    }

    @PutMapping(value = "/put/{countryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String updateCountry(@RequestBody String formData, @PathVariable("countryId") int countryId)
            throws ResourceNotFoundException, JsonProcessingException {
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        String countryName = String.valueOf(linkedHashMap.get("countryName"));
        String countryLink = String.valueOf(linkedHashMap.get("countryLink"));
        Country country = countryService.findById(countryId).orElseThrow(() -> new ResourceNotFoundException("no country found with id " + countryId));
        country.setCountryName(countryName);
        country.setCountryLink(countryLink);
        countryService.save(country);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String createCountry(@RequestBody String formData)
            throws JsonProcessingException {
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        String countryName = String.valueOf(linkedHashMap.get("countryName"));
        String countryLink = String.valueOf(linkedHashMap.get("countryLink"));
        Country country = new Country();
        country.setCountryName(countryName);
        country.setCountryLink(countryLink);
        countryService.save(country);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/countryName/{input}")
    public @ResponseBody
    String readCountries(@PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Country.class, countrySerializer);
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            Optional<Country> country = countryService.findByCountryName(input);
            if (country.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(country));
        } else {
            List<Country> countryList = countryService.findByCountryNameContains(input);
            if (countryList.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(countryList);
        }
    }
}
