package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Content;
import com.nfssoundtrack.racingsoundtracks.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    @Autowired
    ContentRepository contentRepository;

    public Content findByContentShort(String contentShort) {
        return contentRepository.findByContentShort(contentShort);
    }
}
