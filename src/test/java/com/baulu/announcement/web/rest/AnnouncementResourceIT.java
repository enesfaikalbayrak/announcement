package com.baulu.announcement.web.rest;

import static com.baulu.announcement.domain.AnnouncementAsserts.*;
import static com.baulu.announcement.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.baulu.announcement.IntegrationTest;
import com.baulu.announcement.domain.Announcement;
import com.baulu.announcement.domain.enumeration.AnnouncementType;
import com.baulu.announcement.domain.enumeration.Language;
import com.baulu.announcement.repository.AnnouncementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AnnouncementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnnouncementResourceIT {

    private static final Language DEFAULT_LANGUAGE = Language.TURKISH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final AnnouncementType DEFAULT_ANNOUNCEMENT_TYPE = AnnouncementType.TEXT;
    private static final AnnouncementType UPDATED_ANNOUNCEMENT_TYPE = AnnouncementType.IMAGE;

    private static final String DEFAULT_ANNOUNCEMENT_DATA = "AAAAAAAAAA";
    private static final String UPDATED_ANNOUNCEMENT_DATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/announcements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnnouncementMockMvc;

    private Announcement announcement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Announcement createEntity(EntityManager em) {
        Announcement announcement = new Announcement()
            .language(DEFAULT_LANGUAGE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .announcementType(DEFAULT_ANNOUNCEMENT_TYPE)
            .announcementData(DEFAULT_ANNOUNCEMENT_DATA);
        return announcement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Announcement createUpdatedEntity(EntityManager em) {
        Announcement announcement = new Announcement()
            .language(UPDATED_LANGUAGE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .announcementType(UPDATED_ANNOUNCEMENT_TYPE)
            .announcementData(UPDATED_ANNOUNCEMENT_DATA);
        return announcement;
    }

    @BeforeEach
    public void initTest() {
        announcement = createEntity(em);
    }

    @Test
    @Transactional
    void createAnnouncement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Announcement
        var returnedAnnouncement = om.readValue(
            restAnnouncementMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcement))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Announcement.class
        );

        // Validate the Announcement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAnnouncementUpdatableFieldsEquals(returnedAnnouncement, getPersistedAnnouncement(returnedAnnouncement));
    }

    @Test
    @Transactional
    void createAnnouncementWithExistingId() throws Exception {
        // Create the Announcement with an existing ID
        announcement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnnouncementMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcement)))
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAnnouncements() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].announcementType").value(hasItem(DEFAULT_ANNOUNCEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].announcementData").value(hasItem(DEFAULT_ANNOUNCEMENT_DATA.toString())));
    }

    @Test
    @Transactional
    void getAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get the announcement
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL_ID, announcement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(announcement.getId().intValue()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.announcementType").value(DEFAULT_ANNOUNCEMENT_TYPE.toString()))
            .andExpect(jsonPath("$.announcementData").value(DEFAULT_ANNOUNCEMENT_DATA.toString()));
    }

    @Test
    @Transactional
    void getAnnouncementsByIdFiltering() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        Long id = announcement.getId();

        defaultAnnouncementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAnnouncementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAnnouncementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where language equals to
        defaultAnnouncementFiltering("language.equals=" + DEFAULT_LANGUAGE, "language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where language in
        defaultAnnouncementFiltering("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE, "language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where language is not null
        defaultAnnouncementFiltering("language.specified=true", "language.specified=false");
    }

    @Test
    @Transactional
    void getAllAnnouncementsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where startDate equals to
        defaultAnnouncementFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where startDate in
        defaultAnnouncementFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where startDate is not null
        defaultAnnouncementFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAnnouncementsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where endDate equals to
        defaultAnnouncementFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where endDate in
        defaultAnnouncementFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllAnnouncementsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where endDate is not null
        defaultAnnouncementFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAnnouncementsByAnnouncementTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where announcementType equals to
        defaultAnnouncementFiltering(
            "announcementType.equals=" + DEFAULT_ANNOUNCEMENT_TYPE,
            "announcementType.equals=" + UPDATED_ANNOUNCEMENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAnnouncementsByAnnouncementTypeIsInShouldWork() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where announcementType in
        defaultAnnouncementFiltering(
            "announcementType.in=" + DEFAULT_ANNOUNCEMENT_TYPE + "," + UPDATED_ANNOUNCEMENT_TYPE,
            "announcementType.in=" + UPDATED_ANNOUNCEMENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAnnouncementsByAnnouncementTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList where announcementType is not null
        defaultAnnouncementFiltering("announcementType.specified=true", "announcementType.specified=false");
    }

    private void defaultAnnouncementFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAnnouncementShouldBeFound(shouldBeFound);
        defaultAnnouncementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAnnouncementShouldBeFound(String filter) throws Exception {
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].announcementType").value(hasItem(DEFAULT_ANNOUNCEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].announcementData").value(hasItem(DEFAULT_ANNOUNCEMENT_DATA.toString())));

        // Check, that the count call also returns 1
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAnnouncementShouldNotBeFound(String filter) throws Exception {
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAnnouncement() throws Exception {
        // Get the announcement
        restAnnouncementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the announcement
        Announcement updatedAnnouncement = announcementRepository.findById(announcement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAnnouncement are not directly saved in db
        em.detach(updatedAnnouncement);
        updatedAnnouncement
            .language(UPDATED_LANGUAGE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .announcementType(UPDATED_ANNOUNCEMENT_TYPE)
            .announcementData(UPDATED_ANNOUNCEMENT_DATA);

        restAnnouncementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAnnouncement.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAnnouncement))
            )
            .andExpect(status().isOk());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAnnouncementToMatchAllProperties(updatedAnnouncement);
    }

    @Test
    @Transactional
    void putNonExistingAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, announcement.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(announcement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(announcement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAnnouncementWithPatch() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the announcement using partial update
        Announcement partialUpdatedAnnouncement = new Announcement();
        partialUpdatedAnnouncement.setId(announcement.getId());

        partialUpdatedAnnouncement.language(UPDATED_LANGUAGE).endDate(UPDATED_END_DATE);

        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnnouncement.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnnouncement))
            )
            .andExpect(status().isOk());

        // Validate the Announcement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnnouncementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAnnouncement, announcement),
            getPersistedAnnouncement(announcement)
        );
    }

    @Test
    @Transactional
    void fullUpdateAnnouncementWithPatch() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the announcement using partial update
        Announcement partialUpdatedAnnouncement = new Announcement();
        partialUpdatedAnnouncement.setId(announcement.getId());

        partialUpdatedAnnouncement
            .language(UPDATED_LANGUAGE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .announcementType(UPDATED_ANNOUNCEMENT_TYPE)
            .announcementData(UPDATED_ANNOUNCEMENT_DATA);

        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnnouncement.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnnouncement))
            )
            .andExpect(status().isOk());

        // Validate the Announcement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnnouncementUpdatableFieldsEquals(partialUpdatedAnnouncement, getPersistedAnnouncement(partialUpdatedAnnouncement));
    }

    @Test
    @Transactional
    void patchNonExistingAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, announcement.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(announcement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(announcement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(announcement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the announcement
        restAnnouncementMockMvc
            .perform(delete(ENTITY_API_URL_ID, announcement.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return announcementRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Announcement getPersistedAnnouncement(Announcement announcement) {
        return announcementRepository.findById(announcement.getId()).orElseThrow();
    }

    protected void assertPersistedAnnouncementToMatchAllProperties(Announcement expectedAnnouncement) {
        assertAnnouncementAllPropertiesEquals(expectedAnnouncement, getPersistedAnnouncement(expectedAnnouncement));
    }

    protected void assertPersistedAnnouncementToMatchUpdatableProperties(Announcement expectedAnnouncement) {
        assertAnnouncementAllUpdatablePropertiesEquals(expectedAnnouncement, getPersistedAnnouncement(expectedAnnouncement));
    }
}
