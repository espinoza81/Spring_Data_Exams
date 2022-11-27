package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PictureImportDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.Messages;
import softuni.exam.instagraphlite.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl implements PictureService {
    private final double PICTURE_SIZE = 30000;
    private final PictureRepository pictureRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public PictureServiceImpl(
            PictureRepository pictureRepository,
            Gson gson,
            Validator validator,
            ModelMapper mapper) {
        this.pictureRepository = pictureRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(PathFiles.PICTURES_PATH);
    }

    @Override
    public String importPictures() throws IOException {

        final String json = this.readFromFileContent();

        final PictureImportDto[] importPictures = this.gson.fromJson(json, PictureImportDto[].class);

        final List<String> result = new ArrayList<>();

        for (PictureImportDto importPicture : importPictures) {

            final Set<ConstraintViolation<PictureImportDto>> validationErrors =
                    this.validator.validate(importPicture);

            if (validationErrors.isEmpty()) {

                final Optional<Picture> pictureExist = this.pictureRepository.findByPath(importPicture.getPath());

                boolean canAdded = pictureExist.isEmpty();

                if (canAdded) {

                    Picture picture = this.mapper.map(importPicture, Picture.class);

                    this.pictureRepository.save(picture);

                    final String msg = String.format(Messages.IMPORTED_PICTURE, picture.getSize());

                    result.add(msg);

                } else {
                    result.add(Messages.INVALID + Messages.PICTURE);
                }

            } else {
                result.add(Messages.INVALID + Messages.PICTURE);
            }
        }
        return String.join(System.lineSeparator(), result);
    }

    @Override
    public String exportPictures() {
        return this.pictureRepository
                .findAllBySizeGreaterThanOrderBySizeAsc(PICTURE_SIZE)
                .orElseThrow(NoSuchElementException::new)
                .stream()
                .map(Picture::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
