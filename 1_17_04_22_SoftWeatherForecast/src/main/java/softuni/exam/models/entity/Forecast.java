package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.util.Messages;

import javax.persistence.*;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "forecasts")
public class Forecast extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "max_temperature")
    private double maxTemperature;

    @Column(name = "min_temperature")
    private double minTemperature;

    @Column(nullable = false)
    private LocalTime sunrise;

    @Column(nullable = false)
    private LocalTime sunset;

    @ManyToOne
    private City city;

    public String importForecast(){
        return this.dayOfWeek.toString() + Messages.DASH + String.format(Messages.FORMAT_DOUBLE, this.maxTemperature);
    }

    @Override
    public String toString() {
        //City: {cityName}:
        //   		-min temperature: {minTemperature}
        //   		--max temperature: {maxTemperature}
        //   		---sunrise: {sunrise}
        //----sunset: {sunset}
        return String.format(Messages.PRINT_CITY, this.city.getCityName()) + System.lineSeparator() +
             String.format(Messages.PRINT_MIN_TEMPERATURE, this.minTemperature) + System.lineSeparator() +
             String.format(Messages.PRINT_MAX_TEMPERATURE, this.maxTemperature) + System.lineSeparator() +
             String.format(Messages.PRINT_SUNRISE, this.sunrise) + System.lineSeparator() +
             String.format(Messages.PRINT_SUNSET, this.sunset);
    }
}
