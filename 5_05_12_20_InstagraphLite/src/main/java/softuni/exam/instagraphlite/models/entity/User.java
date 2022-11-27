package softuni.exam.instagraphlite.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.instagraphlite.util.Messages;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity{

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_picture_id")
    private Picture profilePicture;

    @OneToMany(mappedBy = "user", targetEntity = Post.class, fetch = FetchType.EAGER)
    private List<Post> posts;

    public User() {
        this.posts = new ArrayList<>();
    }

    public User(String username, String password, Picture profilePicture) {
        this();
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        //User: {username}
        //Post count: {count of posts}
        //==Post Details:
        //----Caption: {caption}
        //----Picture Size: {size}
        return String.format(Messages.PRINT_USER, username) + System.lineSeparator() +
               String.format(Messages.PRINT_POST_COUNT, posts.size()) + System.lineSeparator() +
               posts
                       .stream()
                       .sorted(Comparator.comparingDouble(p -> p.getPicture().getSize()))
                       .map(Post::toString)
                       .collect(Collectors.joining(System.lineSeparator()));
    }
}