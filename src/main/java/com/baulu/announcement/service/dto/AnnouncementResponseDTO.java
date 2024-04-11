package com.baulu.announcement.service.dto;

import com.baulu.announcement.domain.Announcement;

public class AnnouncementResponseDTO {
    private Announcement announcement;
    private String message;

    public AnnouncementResponseDTO() {
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public AnnouncementResponseDTO announcement(Announcement announcement) {
        this.announcement = announcement;
        return this;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public String getMessage() {
        return message;
    }

    public AnnouncementResponseDTO message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AnnouncementResponseDTO{" +
            "announcement=" + announcement +
            ", message='" + message + '\'' +
            '}';
    }
}
