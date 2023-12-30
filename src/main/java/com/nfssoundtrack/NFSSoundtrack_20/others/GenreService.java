package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Service
public class GenreService {

    public Page<SongSubgroup> findPaginated(Pageable pageable, List<SongSubgroup> list) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<SongSubgroup> sublist;
        if (list.size() < startItem) {
            sublist = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            sublist = list.subList(startItem, toIndex);
        }
        Page<SongSubgroup> bookPage
                = new PageImpl<SongSubgroup>(sublist, PageRequest.of(currentPage, pageSize), list.size());
        return bookPage;
    }
}
