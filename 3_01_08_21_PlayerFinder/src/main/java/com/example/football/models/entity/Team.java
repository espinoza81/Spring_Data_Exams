package com.example.football.models.entity;

import com.example.football.util.Messages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "teams")
public class Team extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "stadium_name", nullable = false)
    private String stadiumName;

    @Column(name = "fan_base", nullable = false)
    private int fanBase;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String history;

    @OneToOne
    private Town town;

    @Override
    public String toString() {
        return name + Messages.DASH + fanBase;
    }
}
