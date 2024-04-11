package com.baulu.announcement.service.dto;

import com.baulu.announcement.domain.enumeration.AnnouncementType;
import com.baulu.announcement.domain.enumeration.Language;
import com.baulu.announcement.service.dto.enumaration.RequestType;

import java.io.Serializable;
import java.time.Instant;

public class AnnouncementRequestDTO implements Serializable {
    private Long announcementId;
    private RequestType requestType;
    private Language selectedLanguage;
    private Instant startDate;
    private Instant endDate;
    private AnnouncementType announcementType;
    private String announcementData;

    public AnnouncementRequestDTO() {
    }

    public Long getAnnouncementId() {
        return announcementId;
    }

    public AnnouncementRequestDTO announcementId(Long announcementId) {
        this.announcementId = announcementId;
        return this;
    }

    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public AnnouncementRequestDTO requestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public AnnouncementRequestDTO selectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
        return this;
    }

    public void setSelectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public AnnouncementRequestDTO startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public AnnouncementRequestDTO endDate(Instant endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public AnnouncementType getAnnouncementType() {
        return announcementType;
    }

    public AnnouncementRequestDTO announcementType(AnnouncementType announcementType) {
        this.announcementType = announcementType;
        return this;
    }

    public void setAnnouncementType(AnnouncementType announcementType) {
        this.announcementType = announcementType;
    }

    public String getAnnouncementData() {
        return announcementData;
    }

    public AnnouncementRequestDTO announcementData(String announcementData) {
        this.announcementData = announcementData;
        return this;
    }

    public void setAnnouncementData(String announcementData) {
        this.announcementData = announcementData;
    }

    @Override
    public String toString() {
        return "AnnouncementRequestDTO{" +
            "announcementId=" + announcementId +
            ", requestType=" + requestType +
            ", selectedLanguage=" + selectedLanguage +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", announcementType=" + announcementType +
            ", announcementData='" + announcementData + '\'' +
            '}';
    }
}
