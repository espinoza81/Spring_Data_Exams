package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.util.LocalDateAdapter;

import javax.persistence.Column;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketImportDto {

    @Size(min = 2)
    @XmlElement(name = "serial-number")
    private String serialNumber;

    @Positive
    @XmlElement
    private BigDecimal price;

    @Column
    @XmlElement(name = "take-off")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDateTime takeoff;

    @XmlElement(name = "from-town")
    private TownNameDto fromTown;

    @XmlElement(name = "to-town")
    private TownNameDto toTown;

    @XmlElement
    private PassengerEmailDto passenger;

    @XmlElement
    private PlaneNumberDto plane;
}