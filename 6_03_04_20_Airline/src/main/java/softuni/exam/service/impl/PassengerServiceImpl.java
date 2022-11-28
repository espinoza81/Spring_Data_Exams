package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PassengerImportDto;
import softuni.exam.models.entities.Passenger;
import softuni.exam.models.entities.Town;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.util.Messages;
import softuni.exam.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    private final TownRepository townRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    public PassengerServiceImpl(
            PassengerRepository passengerRepository,
            TownRepository townRepository,
            Gson gson,
            Validator validator,
            ModelMapper mapper) {
        this.passengerRepository = passengerRepository;
        this.townRepository = townRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(PathFiles.PASSENGERS_PATH);
    }

    @Override
    public String importPassengers() throws IOException {
        final String json = this.readPassengersFileContent();

        final PassengerImportDto[] importPassengers = this.gson.fromJson(json, PassengerImportDto[].class);

        final List<String> result = new ArrayList<>();

        for (PassengerImportDto importPassenger : importPassengers) {

            final Set<ConstraintViolation<PassengerImportDto>> validationErrors = this.validator.validate(importPassenger);

            if (validationErrors.isEmpty()) {

                final Optional<Passenger> passengerExist =
                        this.passengerRepository.findByEmail(importPassenger.getEmail());

                final Optional<Town> townExist = this.townRepository.findByName(importPassenger.getTown());

                boolean canAdded = passengerExist.isEmpty() && townExist.isPresent();

                if (canAdded) {

                    Passenger passenger = this.mapper.map(importPassenger, Passenger.class);

                    passenger.setTown(townExist.get());

                    this.passengerRepository.save(passenger);

                    final String msg =
                            Messages.SUCCESSFULLY + Messages.PASSENGER + Messages.INTERVAL + passenger.importInfo();

                    result.add(msg);

                } else {
                    result.add(Messages.INVALID + Messages.PASSENGER);
                }

            } else {
                result.add(Messages.INVALID + Messages.PASSENGER);
            }
        }
        return String.join(System.lineSeparator(), result);
    }

    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {
        return this.passengerRepository
                .findAllByOrderByTicketsDescEmailAsc()
                .orElseThrow(NoSuchElementException::new)
                .stream()
                .map(Passenger::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
