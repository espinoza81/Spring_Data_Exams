package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CityImportDto {

    @Size(min = 2)
    @Size(max = 60)
    private String cityName;

    @Size(min = 2)
    private String description;

    @Min(500)
    private int population;

    private Long country;
}