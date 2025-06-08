package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorMember;
import com.nfssoundtrack.racingsoundtracks.repository.AuthorMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorMemberService {

    private final AuthorMemberRepository authorMemberRepository;

    public AuthorMemberService(AuthorMemberRepository authorMemberRepository) {
        this.authorMemberRepository = authorMemberRepository;
    }

    public List<AuthorMember> findByMember(Author member) {
        return authorMemberRepository.findByMember(member);
    }

    public List<AuthorMember> findByAuthor(Author author) {
        return authorMemberRepository.findByAuthor(author);
    }

    public AuthorMember save(AuthorMember authorMember) {
        return authorMemberRepository.save(authorMember);
    }

    public void delete(AuthorMember authorMember) {
        authorMemberRepository.delete(authorMember);
    }

}
