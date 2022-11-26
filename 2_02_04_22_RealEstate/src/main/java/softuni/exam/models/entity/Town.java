package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.util.Messages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "towns")
public class Town extends BaseEntity{

    @Column(name = "town_name", nullable = false, unique = true)
    private String townName;

    @Column
    private int population;

    @Override
    public String toString() {
        return townName + Messages.DASH + population;
    }
}
//One Agent may have only one Town, but one Town may have many Agents.
//One Apartment may have only one Town, but one Town may have many Apartments.