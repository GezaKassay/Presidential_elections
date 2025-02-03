package com.project.presidential_elections.repository;

import com.project.presidential_elections.entity.Elections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionsRepository extends JpaRepository<Elections, Integer> {
}
