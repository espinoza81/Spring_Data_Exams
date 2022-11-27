package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.UserImportDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.Messages;
import softuni.exam.instagraphlite.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PictureRepository pictureRepository,
            Gson gson,
            Validator validator,
            ModelMapper mapper) {
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(PathFiles.USERS_PATH);
    }

    @Override
    public String importUsers() throws IOException {
        //If the picture is not in the database, do not add the user to the database and return "Invalid User"

        final String json = this.readFromFileContent();

        final UserImportDto[] importUsers = this.gson.fromJson(json, UserImportDto[].class);

        final List<String> result = new ArrayList<>();

        for (UserImportDto importUser : importUsers) {

            final Set<ConstraintViolation<UserImportDto>> validationErrors = this.validator.validate(importUser);

            if (validationErrors.isEmpty()) {

                final Optional<User> userExist = this.userRepository.findByUsername(importUser.getUsername());
                final Optional<Picture> pictureExist = this.pictureRepository.findByPath(importUser.getProfilePicture());

                boolean canAdded = userExist.isEmpty() && pictureExist.isPresent();

                if (canAdded) {

                    User user = this.mapper.map(importUser, User.class);

                    user.setProfilePicture(pictureExist.get());

                    this.userRepository.save(user);

                    final String msg = String.format(Messages.IMPORTED_USER, user.getUsername());

                    result.add(msg);

                } else {
                    result.add(Messages.INVALID + Messages.USER);
                }

            } else {
                result.add(Messages.INVALID + Messages.USER);
            }
        }
        return String.join(System.lineSeparator(), result);
    }

    @Override
    public String exportUsersWithTheirPosts() {
        return this.userRepository
                .findAllByOrderByPostsSizeDescIdAsc()
                .orElseThrow(NoSuchElementException::new)
                .stream()
                .map(User::toString)
                .collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
    }
}
