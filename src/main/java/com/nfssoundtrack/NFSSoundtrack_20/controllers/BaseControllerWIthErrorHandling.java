package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

public class BaseControllerWIthErrorHandling implements ErrorController {

    @Autowired
    private SerieRepository serieRepository;
//
    @RequestMapping(value = "/{otherval}")
    public String nonExistingPagee(Model model, @PathVariable("otherval") String otherval) throws Exception {
        throw new Exception("Tried to access non-existing page: " + otherval);
//        return "redirect:/content/home";
    }

    @ExceptionHandler
    @ResponseBody
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
//        logger.error("Request: " + req.getRequestURL() + " raised " + ex);
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("stacktrace", ex.getStackTrace());
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        mav.addObject("appName", "Error NFSSoundtrack.com");
        mav.addObject("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return mav;
    }
}
