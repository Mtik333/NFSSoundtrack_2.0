package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import com.nfssoundtrack.racingsoundtracks.dbmodel.EntityType;
import com.nfssoundtrack.racingsoundtracks.dbmodel.EntityUrl;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.CountrySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * controller for handling changes to countries
 */
@Controller
@RequestMapping(path = "/country")
public class CountryController extends BaseControllerWithErrorHandling {

    @Autowired
    CountrySerializer countrySerializer;

    /**
     * just to get all countries, used in countryMgmt.js
     * you can see when click on "manage countries"
     *
     * @return json with all countries from database
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/readAll")
    public @ResponseBody
    String readCountries() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = countryService.findAll();
        return objectMapper.writeValueAsString(countries);
    }

    /**
     * method to get country based on its identifier
     * used in countryMgmt.js when you click on 'edit' button in 'manage countries'
     *
     * @param countryId id of ocuntry
     * @return json country entity
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @GetMapping(value = "/read/{countryId}")
    public @ResponseBody
    String readCountry(@PathVariable("countryId") int countryId)
            throws JsonProcessingException, ResourceNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        Country country = countryService.findById(countryId).orElseThrow(
                () -> new ResourceNotFoundException("no country found with id " + countryId));
        return objectMapper.writeValueAsString(country);
    }

    /**
     * method to update country based on its identifier
     * used in countryMgmt.js when you click on 'save' button in 'manage countries'
     *
     * @param formData  consists just of country name and link (hardly possible but country can change name isn't it?)
     * @param countryId id of country in db
     * @return OK if successful
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/put/{countryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String updateCountry(@RequestBody String formData, @PathVariable("countryId") int countryId)
            throws ResourceNotFoundException, JsonProcessingException {
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        String countryName = String.valueOf(linkedHashMap.get("countryName"));
        String countryLink = String.valueOf(linkedHashMap.get("countryLink"));
        String localLink = String.valueOf(linkedHashMap.get("localLink"));
        Country country = countryService.findById(countryId).orElseThrow(
                () -> new ResourceNotFoundException("no country found with id " + countryId));
        //just updating the entity
        country.setCountryName(countryName);
        country.setCountryLink(countryLink);
        country.setLocalLink(localLink.substring(0, 2).toLowerCase() + ".svg");
        String message = "Updating country " + country.getCountryName();
        countryService.saveUpdate(country);
        sendMessageToChannel(EntityType.COUNTRY, "update", message,
                EntityUrl.COUNTRYINFO, country.getCountryName(), String.valueOf(country.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used to create new country in database
     * used in countryMgmt.js when clicking 'new country' in 'manage countries'
     *
     * @param formData consist of country name and link to flag as png
     * @return OK if successful
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String createCountry(@RequestBody String formData)
            throws JsonProcessingException {
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        String countryName = String.valueOf(linkedHashMap.get("countryName"));
        String countryLink = String.valueOf(linkedHashMap.get("countryLink"));
        String localLink = String.valueOf(linkedHashMap.get("localLink"));
        Country country = new Country();
        country.setCountryName(countryName);
        country.setCountryLink(countryLink);
        country.setLocalLink(localLink.substring(0, 2).toLowerCase() + ".svg");
        String message = "Creating country " + country.getCountryName();
        countryService.save(country);
        sendMessageToChannel(EntityType.COUNTRY, "create", message,
                EntityUrl.COUNTRYINFO, country.getCountryName(), String.valueOf(country.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method to get country / countries based on name input
     * used in artistMgmt.js when you type in country name in 'country' field in 'manage artists'
     *
     * @param input text to look for in countries table
     * @return json list of countries matching criteria
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/countryName/{countryNameInput}")
    public @ResponseBody
    String readCountries(@PathVariable("countryNameInput") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Country.class, countrySerializer);
        if (input.length() <= 3) {
            //i dont think we have any 3-letter country but just in case we handle it similarly as in ArtistController
            Optional<Country> country = countryService.findByCountryName(input);
            if (country.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(country));
        } else {
            //for longer input we use 'contains' concept
            List<Country> countryList = countryService.findByCountryNameContains(input);
            if (countryList.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(countryList);
        }
    }
}
