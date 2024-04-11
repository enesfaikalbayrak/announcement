package com.baulu.announcement.service;

import com.baulu.announcement.domain.*; // for static metamodels
import com.baulu.announcement.domain.Announcement;
import com.baulu.announcement.repository.AnnouncementRepository;
import com.baulu.announcement.service.criteria.AnnouncementCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Announcement} entities in the database.
 * The main input is a {@link AnnouncementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Announcement} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AnnouncementQueryService extends QueryService<Announcement> {

    private final Logger log = LoggerFactory.getLogger(AnnouncementQueryService.class);

    private final AnnouncementRepository announcementRepository;

    public AnnouncementQueryService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * Return a {@link Page} of {@link Announcement} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findByCriteria(AnnouncementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Announcement> specification = createSpecification(criteria);
        return announcementRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AnnouncementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Announcement> specification = createSpecification(criteria);
        return announcementRepository.count(specification);
    }

    /**
     * Function to convert {@link AnnouncementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Announcement> createSpecification(AnnouncementCriteria criteria) {
        Specification<Announcement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Announcement_.id));
            }
            if (criteria.getLanguage() != null) {
                specification = specification.and(buildSpecification(criteria.getLanguage(), Announcement_.language));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Announcement_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Announcement_.endDate));
            }
            if (criteria.getAnnouncementType() != null) {
                specification = specification.and(buildSpecification(criteria.getAnnouncementType(), Announcement_.announcementType));
            }
        }
        return specification;
    }
}
