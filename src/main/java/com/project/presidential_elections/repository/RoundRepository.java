package com.project.presidential_elections.repository;

import com.project.presidential_elections.entity.RoundEntity;
import com.project.presidential_elections.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends JpaRepository<RoundEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM RoundEntity r WHERE TYPE(r) = :roundType AND r.user = :user")
    boolean existsByUserAndRoundType(@Param("user") UserEntity user, @Param("roundType") Class<? extends RoundEntity> roundType);
}
