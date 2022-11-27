package exam.service.impl;

import exam.model.dto.TownImportDto;
import exam.model.dto.TownWrapperDto;
import exam.model.entity.Town;
import exam.repository.TownRepository;
import exam.service.TownService;
import exam.util.Messages;
import exam.util.PathFiles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;

    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public TownServiceImpl(
            TownRepository townRepository,
            Validator validator,
            ModelMapper mapper) {
        this.townRepository = townRepository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(PathFiles.TOWNS_PATH);
    }

    @Override
    public String importTowns() throws JAXBException, FileNotFoundException {

        final FileReader fileReader = new FileReader(PathFiles.TOWNS_PATH.toFile());

        final JAXBContext context = JAXBContext.newInstance(TownWrapperDto.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final TownWrapperDto townsDto = (TownWrapperDto) unmarshaller.unmarshal(fileReader);


        return townsDto
                .getTowns()
                .stream()
                .map(this::importTown)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importTown(TownImportDto dto) {

        Set<ConstraintViolation<TownImportDto>> errors =
                this.validator.validate(dto);

        if (!errors.isEmpty()) {
            return Messages.INVALID + Messages.TOWN;
        }

        Optional<Town> townExist = this.townRepository.findByName(dto.getName());

        if (townExist.isPresent()) {
            return Messages.INVALID + Messages.TOWN;
        }

        Town town = this.mapper.map(dto, Town.class);

        this.townRepository.save(town);

        return Messages.SUCCESSFULLY + Messages.TOWN + Messages.INTERVAL + town.getName();
    }
}
