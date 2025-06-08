package com.nfssoundtrack.racingsoundtracks.others;

import com.nfssoundtrack.racingsoundtracks.controllers.WebsiteViewsController;
import com.nfssoundtrack.racingsoundtracks.services.GameService;
import com.nfssoundtrack.racingsoundtracks.services.SerieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final SerieService serieService;
    private final GameService gameService;

    public GlobalExceptionHandler(SerieService serieService, GameService gameService) {
        this.serieService = serieService;
        this.gameService = gameService;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globleExcpetionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * i wouldn't be surprised if this does not work at all anymore
     *
     * @param req default thing
     * @param ex  default thing
     * @return just model with all the games again
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("stacktrace", ex.getStackTrace());
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        mav.addObject(WebsiteViewsController.APP_NAME, "Error NFSSoundtrack.com");
        mav.addObject(WebsiteViewsController.SERIES, serieService.findAllSortedByPositionAsc());
        mav.addObject(WebsiteViewsController.GAMES_ALPHA, gameService.findAllSortedByDisplayTitleAsc());
        return mav;
    }
}
