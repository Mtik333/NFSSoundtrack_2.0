package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorAliasRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/all")
public class AllController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorAliasRepository authorAliasRepository;
    @GetMapping(path="/allauthor")
    public @ResponseBody Iterable<Author> getAllAuthors() {
        // This returns a JSON or XML with the users
        return authorRepository.findAll();
    }

    @GetMapping(path="/allauthoralias")
    public @ResponseBody Iterable<AuthorAlias> getAllUsers() {
        // This returns a JSON or XML with the users
        return authorAliasRepository.findAll();
    }
}
