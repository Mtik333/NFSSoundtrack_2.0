package com.nfssoundtrack.racingsoundtracks.dbmodel;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "platform")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "platform")
    private String platform;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Platform platform)) {
            return false;
        }
        return (long) platform.getId() == id;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", platform='" + platform + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, platform);
    }
}
