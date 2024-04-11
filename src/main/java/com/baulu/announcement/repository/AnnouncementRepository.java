package com.baulu.announcement.repository;

import com.baulu.announcement.domain.Announcement;
import com.baulu.announcement.domain.enumeration.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long>, JpaSpecificationExecutor<Announcement> {

    @Query(" SELECT DISTINCT announcement FROM Announcement announcement " +
        " where announcement.startDate < :date " +
        " and announcement.endDate > :date " +
        " and announcement.language = :selectedLanguage ")
    List<Announcement> findByDateAndLanguage(@Param("date") Instant date,
                                             @Param("selectedLanguage") Language selectedLanguage);
}
