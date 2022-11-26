package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.util.Messages;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "apartments")
public class Apartment extends BaseEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "apartment_type", nullable = false)
    private ApartmentType apartmentType;

    @Column
    private double area;

    @OneToOne
    private Town town;

    @Override
    public String toString() {
        return apartmentType.toString() + Messages.DASH + String.format(Messages.FORMAT_DOUBLE, area);
    }
}
//One Apartment may have only one Town, but one Town may have many Apartments.
//One Offer may have only one Apartment, but one Apartment can be in many Offers.