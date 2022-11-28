package softuni.exam.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.util.Messages;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tickets")
public class Ticket extends BaseEntity{

    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Column
    private BigDecimal price;

    @Column
    private LocalDateTime takeoff;

    @ManyToOne
    @JoinColumn(name = "from_town_id")
    private Town fromTown;

    @ManyToOne
    @JoinColumn(name = "to_town_id")
    private Town toTown;

    @ManyToOne
    private Passenger passenger;

    @ManyToOne
    private Plane plane;

    @Override
    public String toString() {
        return fromTown.getName() + Messages.DASH + toTown.getName();
    }
}