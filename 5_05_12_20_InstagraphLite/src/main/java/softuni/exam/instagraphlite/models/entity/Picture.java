package softuni.exam.instagraphlite.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.instagraphlite.util.Messages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String path;

    @Column
    private double size;

    @Override
    public String toString() {
        return String.format(Messages.PRINT_PICTURE, size, path);
    }
}

//One Picture may have many Users, but one User may have only one Picture.
//One Post may have only one Picture, but one Picture can be in many Posts.