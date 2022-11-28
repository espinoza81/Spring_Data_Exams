package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TicketImportDto;
import softuni.exam.models.dto.TicketWrapperDto;
import softuni.exam.models.entities.Passenger;
import softuni.exam.models.entities.Plane;
import softuni.exam.models.entities.Ticket;
import softuni.exam.models.entities.Town;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.repository.PlaneRepository;
import softuni.exam.repository.TicketRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TicketService;
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
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final PlaneRepository planeRepository;
    private final TownRepository townRepository;
    private final PassengerRepository passengerRepository;

    private final Validator validator;
    private final ModelMapper mapper;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            PlaneRepository planeRepository,
            TownRepository townRepository,
            PassengerRepository passengerRepository,
            Validator validator,
            ModelMapper mapper) {
        this.ticketRepository = ticketRepository;
        this.planeRepository = planeRepository;
        this.townRepository = townRepository;
        this.passengerRepository = passengerRepository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.ticketRepository.count() > 0;
    }

    @Override
    public String readTicketsFileContent() throws IOException {
        return Files.readString(PathFiles.TICKETS_PATH);
    }

    @Override
    public String importTickets() throws FileNotFoundException, JAXBException {
        final FileReader fileReader = new FileReader(PathFiles.TICKETS_PATH.toFile());

        final JAXBContext context = JAXBContext.newInstance(TicketWrapperDto.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final TicketWrapperDto ticketsDto = (TicketWrapperDto) unmarshaller.unmarshal(fileReader);


        return ticketsDto
                .getTickets()
                .stream()
                .map(this::importTicket)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importTicket(TicketImportDto dto) {

        Set<ConstraintViolation<TicketImportDto>> errors = this.validator.validate(dto);

        if (!errors.isEmpty()) {
            return Messages.INVALID + Messages.TICKET;
        }

        final Optional<Ticket> ticketExist = this.ticketRepository.findBySerialNumber(dto.getSerialNumber());

        final Optional<Town> fromTown = this.townRepository.findByName(dto.getFromTown().getName());
        final Optional<Town> toTown = this.townRepository.findByName(dto.getToTown().getName());
        final Optional<Passenger> passenger = this.passengerRepository.findByEmail(dto.getPassenger().getEmail());
        final Optional<Plane> plane = this.planeRepository.findByRegisterNumber(dto.getPlane().getRegisterNumber());


        boolean canNotAdded = ticketExist.isPresent()
                || fromTown.isEmpty()
                || toTown.isEmpty()
                || passenger.isEmpty()
                || plane.isEmpty();

        if (canNotAdded) {
            return Messages.INVALID + Messages.TICKET;
        }

        Ticket ticket = this.mapper.map(dto, Ticket.class);

        ticket.setFromTown(fromTown.get());
        ticket.setToTown(toTown.get());
        ticket.setPassenger(passenger.get());
        ticket.setPlane(plane.get());

        this.ticketRepository.save(ticket);

        return Messages.SUCCESSFULLY + Messages.TICKET + Messages.INTERVAL + ticket;
    }
}
