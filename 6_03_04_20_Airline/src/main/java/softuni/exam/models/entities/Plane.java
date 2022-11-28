package softuni.exam.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "planes")
public class Plane extends BaseEntity{

    @Column(name = "register_number", unique = true)
    private String registerNumber;

    @Column
    private int capacity;

    @Column
    private String airline;
}
