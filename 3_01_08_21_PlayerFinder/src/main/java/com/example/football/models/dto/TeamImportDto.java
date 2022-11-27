package com.example.football.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeamImportDto {

    @Size(min = 3)
    private String name;

    @Size(min = 3)
    private String stadiumName;

    @Min(1000)
    private int fanBase;

    @Size(min = 10)
    private String history;

    @NotNull
    private String townName;
}
