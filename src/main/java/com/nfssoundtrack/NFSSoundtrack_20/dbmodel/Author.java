package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "author")
@NamedEntityGraph(name = "Author.authorCountries", attributeNodes = @NamedAttributeNode("authorCountries"))
public class Author implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<AuthorCountry> authorCountries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AuthorCountry> getAuthorCountries() {
        return authorCountries;
    }

    public void setAuthorCountries(List<AuthorCountry> authorCountries) {
        this.authorCountries = authorCountries;
    }
}