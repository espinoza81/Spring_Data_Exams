package softuni.exam.models.entities;

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

    @Column(unique = true)
    private String name;

    @Column
    private int population;

    @Column(columnDefinition = "TEXT")
    private String guide;

    @Override
    public String toString() {
        return name + Messages.DASH + population;
    }
}