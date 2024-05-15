package com.baulu.announcement.web.rest;

import com.baulu.announcement.domain.Announcement;
import com.baulu.announcement.domain.enumeration.Language;
import com.baulu.announcement.repository.AnnouncementRepository;
import com.baulu.announcement.service.AnnouncementQueryService;
import com.baulu.announcement.service.AnnouncementService;
import com.baulu.announcement.service.criteria.AnnouncementCriteria;
import com.baulu.announcement.service.dto.AnnouncementRequestDTO;
import com.baulu.announcement.service.dto.AnnouncementResponseDTO;
import com.baulu.announcement.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.baulu.announcement.domain.Announcement}.
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementResource {

    private final Logger log = LoggerFactory.getLogger(AnnouncementResource.class);

    private static final String ENTITY_NAME = "bauluAnnouncementAnnouncement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnnouncementService announcementService;

    private final AnnouncementRepository announcementRepository;

    private final AnnouncementQueryService announcementQueryService;

    public AnnouncementResource(
        AnnouncementService announcementService,
        AnnouncementRepository announcementRepository,
        AnnouncementQueryService announcementQueryService
    ) {
        this.announcementService = announcementService;
        this.announcementRepository = announcementRepository;
        this.announcementQueryService = announcementQueryService;
    }

    /**
     * {@code POST  /announcements} : Create a new announcement.
     *
     * @param announcement the announcement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new announcement, or with status {@code 400 (Bad Request)} if the announcement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) throws URISyntaxException {
        log.debug("REST request to save Announcement : {}", announcement);
        if (announcement.getId() != null) {
            throw new BadRequestAlertException("A new announcement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        announcement = announcementService.save(announcement);
        return ResponseEntity.created(new URI("/api/announcements/" + announcement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, announcement.getId().toString()))
            .body(announcement);
    }

    /**
     * {@code PUT  /announcements/:id} : Updates an existing announcement.
     *
     * @param id the id of the announcement to save.
     * @param announcement the announcement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated announcement,
     * or with status {@code 400 (Bad Request)} if the announcement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the announcement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Announcement> updateAnnouncement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Announcement announcement
    ) throws URISyntaxException {
        log.debug("REST request to update Announcement : {}, {}", id, announcement);
        if (announcement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, announcement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!announcementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        announcement = announcementService.update(announcement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, announcement.getId().toString()))
            .body(announcement);
    }

    /**
     * {@code PATCH  /announcements/:id} : Partial updates given fields of an existing announcement, field will ignore if it is null
     *
     * @param id the id of the announcement to save.
     * @param announcement the announcement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated announcement,
     * or with status {@code 400 (Bad Request)} if the announcement is not valid,
     * or with status {@code 404 (Not Found)} if the announcement is not found,
     * or with status {@code 500 (Internal Server Error)} if the announcement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Announcement> partialUpdateAnnouncement(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Announcement announcement
    ) throws URISyntaxException {
        log.debug("REST request to partial update Announcement partially : {}, {}", id, announcement);
        if (announcement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, announcement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!announcementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Announcement> result = announcementService.partialUpdate(announcement);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, announcement.getId().toString())
        );
    }

    /**
     * {@code GET  /announcements} : get all the announcements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of announcements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Announcement>> getAllAnnouncements(
        AnnouncementCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Announcements by criteria: {}", criteria);

        Page<Announcement> page = announcementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /announcements/count} : count all the announcements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAnnouncements(AnnouncementCriteria criteria) {
        log.debug("REST request to count Announcements by criteria: {}", criteria);
        return ResponseEntity.ok().body(announcementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /announcements/:id} : get the "id" announcement.
     *
     * @param id the id of the announcement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the announcement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable("id") Long id) {
        log.debug("REST request to get Announcement : {}", id);
        Optional<Announcement> announcement = announcementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(announcement);
    }

    /**
     * {@code DELETE  /announcements/:id} : delete the "id" announcement.
     *
     * @param id the id of the announcement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable("id") Long id) {
        log.debug("REST request to delete Announcement : {}", id);
        announcementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/create")
    public ResponseEntity<AnnouncementResponseDTO> createAnnouncement(@RequestBody AnnouncementRequestDTO announcementRequestDTO) throws URISyntaxException {
        log.debug("Rest request to create announcement with announcement request: {}", announcementRequestDTO);

        AnnouncementResponseDTO response = this.announcementService.createAnnouncement(announcementRequestDTO);

        return ResponseEntity.created(new URI("/api/announcements/create" + response.getAnnouncement().getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, response.getAnnouncement().getId().toString()))
            .body(response);
    }

    @PostMapping("/update")
    public ResponseEntity<AnnouncementResponseDTO> updateAnnouncement(@RequestBody AnnouncementRequestDTO announcementRequestDTO) {
        log.debug("Rest request to update announcement with announcement request: {}", announcementRequestDTO);

        AnnouncementResponseDTO response = this.announcementService.updateAnnouncement(announcementRequestDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, response.getAnnouncement().getId().toString()))
            .body(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteAnnouncement(@RequestBody AnnouncementRequestDTO announcementRequestDTO) {
        log.debug("Rest request to delete announcement with announcement request: {}", announcementRequestDTO);

        this.announcementService.deleteAnnouncement(announcementRequestDTO);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, announcementRequestDTO.getAnnouncementId().toString()))
            .build();
    }

    @GetMapping("/get/all/active")
    public ResponseEntity<List<Announcement>> getAnnouncementsByDateAndLanguage(@RequestParam(value = "date") Instant date,
                                                                                @RequestParam(value = "selectedLanguage")Language selectedLanguage) {
        log.debug("Rest request to get announcement by date: {} and language: {}", date, selectedLanguage);
        List<Announcement> announcements = this.announcementService.getAnnouncementsByDateAndLanguage(date, selectedLanguage);
        return ResponseEntity.ok().body(announcements);
    }
}
