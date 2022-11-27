package softuni.exam.models.entity;

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
@Setter
@Getter
@Entity
@Table(name = "cities")
public class City extends BaseEntity{

    @Column(name = "city_name", unique = true, nullable = false)
    private String cityName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private int population;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    @Override
    public String toString() {
        return cityName + " - " + population;
    }
}
//One Forecast may have only one City, but one City may have many Forecasts.
