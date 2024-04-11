package com.baulu.announcement.service.criteria;

import com.baulu.announcement.domain.enumeration.AnnouncementType;
import com.baulu.announcement.domain.enumeration.Language;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.baulu.announcement.domain.Announcement} entity. This class is used
 * in {@link com.baulu.announcement.web.rest.AnnouncementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /announcements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AnnouncementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Language
     */
    public static class LanguageFilter extends Filter<Language> {

        public LanguageFilter() {}

        public LanguageFilter(LanguageFilter filter) {
            super(filter);
        }

        @Override
        public LanguageFilter copy() {
            return new LanguageFilter(this);
        }
    }

    /**
     * Class for filtering AnnouncementType
     */
    public static class AnnouncementTypeFilter extends Filter<AnnouncementType> {

        public AnnouncementTypeFilter() {}

        public AnnouncementTypeFilter(AnnouncementTypeFilter filter) {
            super(filter);
        }

        @Override
        public AnnouncementTypeFilter copy() {
            return new AnnouncementTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LanguageFilter language;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private AnnouncementTypeFilter announcementType;

    private Boolean distinct;

    public AnnouncementCriteria() {}

    public AnnouncementCriteria(AnnouncementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.language = other.optionalLanguage().map(LanguageFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(InstantFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(InstantFilter::copy).orElse(null);
        this.announcementType = other.optionalAnnouncementType().map(AnnouncementTypeFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AnnouncementCriteria copy() {
        return new AnnouncementCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LanguageFilter getLanguage() {
        return language;
    }

    public Optional<LanguageFilter> optionalLanguage() {
        return Optional.ofNullable(language);
    }

    public LanguageFilter language() {
        if (language == null) {
            setLanguage(new LanguageFilter());
        }
        return language;
    }

    public void setLanguage(LanguageFilter language) {
        this.language = language;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public Optional<InstantFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            setStartDate(new InstantFilter());
        }
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public Optional<InstantFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            setEndDate(new InstantFilter());
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public AnnouncementTypeFilter getAnnouncementType() {
        return announcementType;
    }

    public Optional<AnnouncementTypeFilter> optionalAnnouncementType() {
        return Optional.ofNullable(announcementType);
    }

    public AnnouncementTypeFilter announcementType() {
        if (announcementType == null) {
            setAnnouncementType(new AnnouncementTypeFilter());
        }
        return announcementType;
    }

    public void setAnnouncementType(AnnouncementTypeFilter announcementType) {
        this.announcementType = announcementType;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AnnouncementCriteria that = (AnnouncementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(language, that.language) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(announcementType, that.announcementType) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, startDate, endDate, announcementType, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AnnouncementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLanguage().map(f -> "language=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalAnnouncementType().map(f -> "announcementType=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
