package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PostImportDto;
import softuni.exam.instagraphlite.models.dto.PostWrapperDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.models.entity.Post;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.util.Messages;
import softuni.exam.instagraphlite.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;

    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public PostServiceImpl(
            PostRepository postRepository,
            UserRepository userRepository,
            PictureRepository pictureRepository,
            Validator validator,
            ModelMapper mapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(PathFiles.POSTS_PATH);
    }

    @Override
    public String importPosts() throws IOException, JAXBException {

        final FileReader fileReader = new FileReader(PathFiles.POSTS_PATH.toFile());

        final JAXBContext context = JAXBContext.newInstance(PostWrapperDto.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final PostWrapperDto postsDto = (PostWrapperDto) unmarshaller.unmarshal(fileReader);


        return postsDto
                .getPosts()
                .stream()
                .map(this::importPost)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importPost(PostImportDto dto) {

        Set<ConstraintViolation<PostImportDto>> errors = this.validator.validate(dto);

        if (!errors.isEmpty()) {
            return Messages.INVALID + Messages.POST;
        }

        final Optional<User> userExist = this.userRepository.findByUsername(dto.getUser().getUsername());
        final Optional<Picture> pictureExist = this.pictureRepository.findByPath(dto.getPicture().getPath());

        boolean canNotAdded = userExist.isEmpty() || pictureExist.isEmpty();

        if (canNotAdded) {
            return Messages.INVALID + Messages.POST;
        }

        Post post = this.mapper.map(dto, Post.class);

        post.setUser(userExist.get());
        post.setPicture(pictureExist.get());

        this.postRepository.save(post);

        return String.format(Messages.IMPORTED_POST, post.getUser().getUsername());
    }
}
