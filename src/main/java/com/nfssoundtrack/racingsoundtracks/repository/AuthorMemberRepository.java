package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorMemberRepository extends JpaRepository<AuthorMember, Integer> {

    List<AuthorMember> findByMember(Author member);

    List<AuthorMember> findByAuthor(Author author);
}
