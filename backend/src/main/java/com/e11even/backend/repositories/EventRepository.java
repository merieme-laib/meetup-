package com.e11even.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.e11even.backend.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatorId(Long creatorId);

    @Query("SELECT e FROM Event e WHERE " +
           "(:q IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:city IS NULL OR LOWER(e.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:isOnline IS NULL OR e.isOnline = :isOnline)")
    List<Event> search(@Param("q") String q,
                       @Param("category") String category,
                       @Param("city") String city,
                       @Param("isOnline") Boolean isOnline);
}
