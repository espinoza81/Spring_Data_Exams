package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.util.Messages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "agents")
public class Agent extends BaseEntity{

    @Column(name = "first_name", nullable = false, unique = true)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne
    private Town town;

    @Override
    public String toString() {
        return firstName + Messages.INTERVAL + lastName;
    }
}
//One Agent may have only one Town, but one Town may have many Agents.
//One Offer may have only one Agent, but one Agent can have many Offers.