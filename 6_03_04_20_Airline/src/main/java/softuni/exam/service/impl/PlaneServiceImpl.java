package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PlaneImportDto;
import softuni.exam.models.dto.PlaneWrapperDto;
import softuni.exam.models.entities.Plane;
import softuni.exam.repository.PlaneRepository;
import softuni.exam.service.PlaneService;
import softuni.exam.util.Messages;
import softuni.exam.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlaneServiceImpl implements PlaneService {

    private final PlaneRepository planeRepository;

    private final Validator validator;
    private final ModelMapper mapper;

    public PlaneServiceImpl(
            PlaneRepository planeRepository,
            Validator validator,
            ModelMapper mapper) {
        this.planeRepository = planeRepository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.planeRepository.count() > 0;
    }

    @Override
    public String readPlanesFileContent() throws IOException {
        return Files.readString(PathFiles.PLANES_PATH);
    }

    @Override
    public String importPlanes() throws FileNotFoundException, JAXBException {
        final FileReader fileReader = new FileReader(PathFiles.PLANES_PATH.toFile());

        final JAXBContext context = JAXBContext.newInstance(PlaneWrapperDto.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final PlaneWrapperDto planesDto = (PlaneWrapperDto) unmarshaller.unmarshal(fileReader);


        return planesDto
                .getPlanes()
                .stream()
                .map(this::importPlane)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importPlane(PlaneImportDto dto) {

        Set<ConstraintViolation<PlaneImportDto>> errors = this.validator.validate(dto);

        if (!errors.isEmpty()) {
            return Messages.INVALID + Messages.PLANE;
        }

        final Optional<Plane> planeExist = this.planeRepository.findByRegisterNumber(dto.getRegisterNumber());


        boolean canNotAdded = planeExist.isPresent();

        if (canNotAdded) {
            return Messages.INVALID + Messages.PLANE;
        }

        Plane plane = this.mapper.map(dto, Plane.class);

        this.planeRepository.save(plane);

        return Messages.SUCCESSFULLY + Messages.PLANE + Messages.INTERVAL + plane.getRegisterNumber();
    }
}
