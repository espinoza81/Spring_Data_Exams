package softuni.exam.models.entities;

import lombok.Getter;
import lombok.Setter;
import softuni.exam.util.Messages;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "passengers")
public class Passenger extends BaseEntity{

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private int age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @ManyToOne
    private Town town;

    @OneToMany(targetEntity = Ticket.class, mappedBy = "passenger")
    private List<Ticket> tickets;

    public String importInfo() {
        return lastName + Messages.DASH + email;
    }

    @Override
    public String toString() {
        ////Passenger {firstName}  {lastName}
        ////	Email - {email}
        ////Phone - {phoneNumber}
        ////	Number of tickets - {number of tickets}

        return String.format(Messages.PRINT_PASSENGER, firstName, lastName) + System.lineSeparator() +
             String.format(Messages.PRINT_EMAIL, email) + System.lineSeparator() +
             String.format(Messages.PRINT_PHONE, phoneNumber) + System.lineSeparator() +
             String.format(Messages.PRINT_TICKETS_NUMBER, tickets.size());
    }

    public Passenger() {
        this.tickets = new ArrayList<>();
    }

    public Passenger(String firstName, String lastName, int age, String phoneNumber, String email, Town town) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.town = town;
    }
}