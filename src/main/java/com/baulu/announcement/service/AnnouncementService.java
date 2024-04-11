package com.baulu.announcement.service;

import com.baulu.announcement.domain.Announcement;
import com.baulu.announcement.domain.enumeration.Language;
import com.baulu.announcement.service.dto.AnnouncementRequestDTO;
import com.baulu.announcement.service.dto.AnnouncementResponseDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.baulu.announcement.domain.Announcement}.
 */
public interface AnnouncementService {
    /**
     * Save a announcement.
     *
     * @param announcement the entity to save.
     * @return the persisted entity.
     */
    Announcement save(Announcement announcement);

    /**
     * Updates a announcement.
     *
     * @param announcement the entity to update.
     * @return the persisted entity.
     */
    Announcement update(Announcement announcement);

    /**
     * Partially updates a announcement.
     *
     * @param announcement the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Announcement> partialUpdate(Announcement announcement);

    /**
     * Get the "id" announcement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Announcement> findOne(Long id);

    /**
     * Delete the "id" announcement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO announcementRequestDTO);

    AnnouncementResponseDTO updateAnnouncement(AnnouncementRequestDTO announcementRequestDTO);

    void deleteAnnouncement(AnnouncementRequestDTO announcementRequestDTO);

    List<Announcement> getAnnouncementsByDateAndLanguage(Instant date, Language selectedLanguage);
}
