package com.nfssoundtrack.racingsoundtracks.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "changelog")
public class Changelog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "log_text")
    private String logText;

    @Column(name = "operation_type")
    private String operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_url")
    private EntityUrl entityUrl;

    @Column(name = "url_label")
    private String urlLabel;

    @Column(name = "url_value")
    private String urlValue;

    @Column(name = "log_date")
    private Timestamp logDate;

    public Changelog() {
    }

    public Changelog(String logText, String operationType, EntityType entityType,
                     EntityUrl entityUrl, String urlLabel, String urlValue, Timestamp logDate) {
        this.logText = logText;
        this.operationType = operationType;
        this.entityType = entityType;
        this.entityUrl = entityUrl;
        this.urlLabel = urlLabel;
        this.urlValue = urlValue;
        this.logDate = logDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Timestamp getLogDate() {
        return logDate;
    }

    public void setLogDate(Timestamp logDate) {
        this.logDate = logDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public EntityUrl getEntityUrl() {
        return entityUrl;
    }

    public void setEntityUrl(EntityUrl entityUrl) {
        this.entityUrl = entityUrl;
    }

    public String getUrlLabel() {
        return urlLabel;
    }

    public void setUrlLabel(String urlLabel) {
        this.urlLabel = urlLabel;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public void setUrlValue(String urlValue) {
        this.urlValue = urlValue;
    }
}
