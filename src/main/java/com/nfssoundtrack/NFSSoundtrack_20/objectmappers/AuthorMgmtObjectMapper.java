package com.nfssoundtrack.NFSSoundtrack_20.objectmappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.ArtistMgmtSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.services.AuthorAliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@JsonComponent
public class AuthorMgmtObjectMapper extends ObjectMapper {

    public AuthorMgmtObjectMapper() {
        super();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Author.class, new ArtistMgmtSerializer());
        this.registerModule(simpleModule);
    }
}
