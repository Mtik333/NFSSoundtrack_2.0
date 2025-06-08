package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Changelog;
import com.nfssoundtrack.racingsoundtracks.repository.ChangelogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangelogService {

    private final ChangelogRepository changelogRepository;

    public ChangelogService(ChangelogRepository changelogRepository) {
        this.changelogRepository = changelogRepository;
    }

    public Changelog save(Changelog changelog) {
        return changelogRepository.save(changelog);
    }

    public Changelog saveUpdate(Changelog changelog) {
        return changelogRepository.save(changelog);
    }

    public List<Changelog> findAll() {
        return changelogRepository.findAll();
    }

    public Page<Changelog> findAll(int page) {
        return changelogRepository.findAll(PageRequest.of(0,page,Sort.by(Sort.Direction.DESC, "id")));
    }

}
