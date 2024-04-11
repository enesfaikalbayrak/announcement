package com.baulu.announcement.service.impl;

import com.baulu.announcement.domain.Announcement;
import com.baulu.announcement.domain.enumeration.Language;
import com.baulu.announcement.repository.AnnouncementRepository;
import com.baulu.announcement.service.AnnouncementService;
import com.baulu.announcement.service.dto.AnnouncementRequestDTO;
import com.baulu.announcement.service.dto.AnnouncementResponseDTO;
import com.baulu.announcement.service.dto.enumaration.RequestType;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link com.baulu.announcement.domain.Announcement}.
 */
@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final Logger log = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public Announcement save(Announcement announcement) {
        log.debug("Request to save Announcement : {}", announcement);
        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement update(Announcement announcement) {
        log.debug("Request to update Announcement : {}", announcement);
        return announcementRepository.save(announcement);
    }

    @Override
    public Optional<Announcement> partialUpdate(Announcement announcement) {
        log.debug("Request to partially update Announcement : {}", announcement);

        return announcementRepository
            .findById(announcement.getId())
            .map(existingAnnouncement -> {
                if (announcement.getLanguage() != null) {
                    existingAnnouncement.setLanguage(announcement.getLanguage());
                }
                if (announcement.getStartDate() != null) {
                    existingAnnouncement.setStartDate(announcement.getStartDate());
                }
                if (announcement.getEndDate() != null) {
                    existingAnnouncement.setEndDate(announcement.getEndDate());
                }
                if (announcement.getAnnouncementType() != null) {
                    existingAnnouncement.setAnnouncementType(announcement.getAnnouncementType());
                }
                if (announcement.getAnnouncementData() != null) {
                    existingAnnouncement.setAnnouncementData(announcement.getAnnouncementData());
                }

                return existingAnnouncement;
            })
            .map(announcementRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Announcement> findOne(Long id) {
        log.debug("Request to get Announcement : {}", id);
        return announcementRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Announcement : {}", id);
        announcementRepository.deleteById(id);
    }

    @Override
    public AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO announcementRequestDTO) throws IllegalArgumentException {
        validateRequest(announcementRequestDTO, RequestType.CREATE);

        Announcement newAnnouncement = new Announcement()
            .announcementData(announcementRequestDTO.getAnnouncementData())
            .announcementType(announcementRequestDTO.getAnnouncementType())
            .endDate(announcementRequestDTO.getEndDate())
            .startDate(announcementRequestDTO.getStartDate())
            .language(announcementRequestDTO.getSelectedLanguage());

        newAnnouncement = announcementRepository.save(newAnnouncement);

        return new AnnouncementResponseDTO()
            .announcement(newAnnouncement)
            .message("Announcement Successfully Created With Id: " + newAnnouncement.getId());
    }

    @Override
    public AnnouncementResponseDTO updateAnnouncement(AnnouncementRequestDTO announcementRequestDTO) {
        validateRequest(announcementRequestDTO, RequestType.UPDATE);

        Optional<Announcement> optionalOldAnnouncement = this.announcementRepository.findById(announcementRequestDTO.getAnnouncementId());
        if (!optionalOldAnnouncement.isPresent()) {
            throw new NotFoundException("There isn't any announcement with id: " + announcementRequestDTO.getAnnouncementId());
        }

        Announcement updatedAnnouncement = optionalOldAnnouncement.orElseThrow(() -> new IllegalArgumentException("Id can not be null"));

        updatedAnnouncement.announcementData(announcementRequestDTO.getAnnouncementData())
            .announcementType(announcementRequestDTO.getAnnouncementType())
            .language(announcementRequestDTO.getSelectedLanguage())
            .startDate(announcementRequestDTO.getStartDate())
            .endDate(announcementRequestDTO.getEndDate());

        updatedAnnouncement = this.announcementRepository.save(updatedAnnouncement);

        return new AnnouncementResponseDTO()
            .announcement(updatedAnnouncement)
            .message("Announcement Successfully Updated With Id: " + updatedAnnouncement.getId());
    }

    @Override
    public void deleteAnnouncement(AnnouncementRequestDTO announcementRequestDTO) {
        if (announcementRequestDTO.getRequestType() != RequestType.DELETE) {
            throw new IllegalArgumentException("Request type must be DELETE");
        }

        Optional.ofNullable(announcementRequestDTO.getAnnouncementId())
            .orElseThrow(() -> new IllegalArgumentException("Id can not be null"));

        this.announcementRepository.deleteById(announcementRequestDTO.getAnnouncementId());
    }

    @Override
    public List<Announcement> getAnnouncementsByDateAndLanguage(Instant date, Language selectedLanguage) {
        log.info("Retrieving announcements for date: {} and language: {}", date, selectedLanguage);

        if (date == null) {
            throw new IllegalArgumentException("Date parameter cannot be null");
        }
        if (selectedLanguage == null) {
            throw new IllegalArgumentException("Language parameter cannot be null");
        }

        List<Announcement> announcements = announcementRepository.findByDateAndLanguage(date, selectedLanguage);

        if (announcements.isEmpty()) {
            log.info("No announcements found for date: {} and language: {}", date, selectedLanguage);
        } else {
            log.info("{} announcements found for date: {} and language: {}", announcements.size(), date, selectedLanguage);
        }

        return announcements;
    }


    private void validateRequest(AnnouncementRequestDTO requestDTO, RequestType expectedType) {
        if (requestDTO.getRequestType() != expectedType) {
            throw new IllegalArgumentException("Request type must be " + expectedType);
        }

        validateCommonFields(requestDTO);
    }

    private void validateCommonFields(AnnouncementRequestDTO requestDTO) {
        requireNonNull(requestDTO.getAnnouncementType(), "Announcement Type cannot be null");
        requireNonEmpty(requestDTO.getAnnouncementData(), "Announcement Data cannot be null or empty");
        requireNonNull(requestDTO.getSelectedLanguage(), "Selected language cannot be null");
        requireNonNull(requestDTO.getStartDate(), "Start date cannot be null");
        requireNonNull(requestDTO.getEndDate(), "End date cannot be null");
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireNonEmpty(String str, String message) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
