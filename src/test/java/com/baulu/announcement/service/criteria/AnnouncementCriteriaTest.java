package com.baulu.announcement.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AnnouncementCriteriaTest {

    @Test
    void newAnnouncementCriteriaHasAllFiltersNullTest() {
        var announcementCriteria = new AnnouncementCriteria();
        assertThat(announcementCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void announcementCriteriaFluentMethodsCreatesFiltersTest() {
        var announcementCriteria = new AnnouncementCriteria();

        setAllFilters(announcementCriteria);

        assertThat(announcementCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void announcementCriteriaCopyCreatesNullFilterTest() {
        var announcementCriteria = new AnnouncementCriteria();
        var copy = announcementCriteria.copy();

        assertThat(announcementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(announcementCriteria)
        );
    }

    @Test
    void announcementCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var announcementCriteria = new AnnouncementCriteria();
        setAllFilters(announcementCriteria);

        var copy = announcementCriteria.copy();

        assertThat(announcementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(announcementCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var announcementCriteria = new AnnouncementCriteria();

        assertThat(announcementCriteria).hasToString("AnnouncementCriteria{}");
    }

    private static void setAllFilters(AnnouncementCriteria announcementCriteria) {
        announcementCriteria.id();
        announcementCriteria.language();
        announcementCriteria.startDate();
        announcementCriteria.endDate();
        announcementCriteria.announcementType();
        announcementCriteria.distinct();
    }

    private static Condition<AnnouncementCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLanguage()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getAnnouncementType()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AnnouncementCriteria> copyFiltersAre(
        AnnouncementCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLanguage(), copy.getLanguage()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getAnnouncementType(), copy.getAnnouncementType()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
