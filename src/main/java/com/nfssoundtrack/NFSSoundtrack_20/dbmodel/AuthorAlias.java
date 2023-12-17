package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

@Entity(name="author_alias")
public class AuthorAlias {

    @Id
    @Column(name="id", nullable=false)
    private Long id;

    @OneToOne(optional=false)
    @JoinColumn(name="author_id")
    private Author author;

    @Column(name="alias")
    private String alias;
}
