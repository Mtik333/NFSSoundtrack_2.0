package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.services.AuthorAliasService;
import com.nfssoundtrack.NFSSoundtrack_20.services.AuthorService;
import com.nfssoundtrack.NFSSoundtrack_20.services.AuthorSongService;
import com.nfssoundtrack.NFSSoundtrack_20.services.ContentService;
import com.nfssoundtrack.NFSSoundtrack_20.services.GameService;
import com.nfssoundtrack.NFSSoundtrack_20.services.GenreService;
import com.nfssoundtrack.NFSSoundtrack_20.services.SerieService;
import com.nfssoundtrack.NFSSoundtrack_20.services.SongGenreService;
import com.nfssoundtrack.NFSSoundtrack_20.services.SongService;
import com.nfssoundtrack.NFSSoundtrack_20.services.SongSubgroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

public class BaseControllerWithErrorHandling implements ErrorController {

	private static final Logger logger = LoggerFactory.getLogger(BaseControllerWithErrorHandling.class);

	@Autowired
	SerieService serieService;

	@Autowired
	ContentService contentService;

	@Autowired
	GameService gameService;

    @Autowired
    GenreService genreService;

    @Autowired
	SongSubgroupService songSubgroupService;

	@Autowired
	SongService songService;

	@Autowired
	AuthorService authorService;

	@Autowired
	AuthorAliasService authorAliasService;

	@Autowired
	AuthorSongService authorSongService;

	@Autowired
	SongGenreService songGenreService;

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
		mav.addObject("series", serieService.findAllSortedByPositionAsc());
		return mav;
	}
}
