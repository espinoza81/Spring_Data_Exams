package softuni.exam.instagraphlite.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.instagraphlite.util.Messages;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String caption;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "picture_id")
    private Picture picture;

    @Override
    public String toString() {
        //==Post Details:
        //----Caption: {caption}
        //----Picture Size: {size}
        return Messages.PRINT_POST_DETAILS + System.lineSeparator() +
               String.format(Messages.PRINT_CAPTION, caption) + System.lineSeparator() +
               String.format(Messages.PRINT_PICTURE_SIZE, picture.getSize());
    }
}