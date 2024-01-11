package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Content;
import com.nfssoundtrack.NFSSoundtrack_20.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

	@Autowired
	ContentRepository contentRepository;

	public Content findByContentShort(String contentShort){
		return contentRepository.findByContentShort(contentShort);
	}
}
