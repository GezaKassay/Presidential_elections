package com.project.presidential_elections.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DynamicRound extends RoundEntity {

    @Column(nullable = false)
    private String type; // Stores the dynamic "subclass" name

    public DynamicRound(String type, Integer numVotes, Integer voted, Integer isCandidate, UserEntity user) {
        super(0, numVotes, voted, isCandidate, user);
        this.type = type;
    }
}