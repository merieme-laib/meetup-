package com.e11even.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e11even.backend.models.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<Like> findByUserIdAndEventId(Long userId, Long eventId);

    List<Like> findByUserId(Long userId);

    long countByEventId(Long eventId);
}
