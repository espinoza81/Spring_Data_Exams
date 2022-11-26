package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ForecastImportDto;
import softuni.exam.models.dto.ForecastWrapperImportDto;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.DayOfWeek;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;
import softuni.exam.util.Messages;
import softuni.exam.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ForecastServiceImpl implements ForecastService {
    private final DayOfWeek dayOfWeek = DayOfWeek.SUNDAY;
    private final int population = 150000;

    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;

    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;

    @Autowired
    public ForecastServiceImpl(
            ForecastRepository forecastRepository,
            CityRepository cityRepository,
            Validator validator,
            ModelMapper modelMapper) throws JAXBException {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;

        JAXBContext context = JAXBContext.newInstance(ForecastWrapperImportDto.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(PathFiles.FORECASTS_PATH);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {

        final FileReader fileReader = new FileReader(PathFiles.FORECASTS_PATH.toFile());

        final ForecastWrapperImportDto forecastsDTOs = (ForecastWrapperImportDto) this.unmarshaller.unmarshal(fileReader);

        return forecastsDTOs
                .getForecasts()
                .stream()
                .map(this::importForecast)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String exportForecasts() {
        Optional<List<Forecast>> forecastsOpt =
                this.forecastRepository
                        .findByDayOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc(dayOfWeek, population);

        List<Forecast> forecasts = forecastsOpt.orElseThrow(NoSuchElementException::new);

        return forecasts
                .stream()
                .map(Forecast::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importForecast(ForecastImportDto dto) {
        Set<ConstraintViolation<ForecastImportDto>> errors =
                this.validator.validate(dto);

        if (!errors.isEmpty()) {
            return Messages.INVALID + Messages.FORECAST;
        }

        Optional<Forecast> forecastExist =
                this.forecastRepository.findByCity_IdAndDayOfWeek(dto.getCity(), dto.getDayOfWeek());

        Optional<City> city = this.cityRepository.findById(dto.getCity());

        if (forecastExist.isPresent() || city.isEmpty()) {
            return Messages.INVALID + Messages.FORECAST;
        }

        Forecast forecast = this.modelMapper.map(dto, Forecast.class);

        forecast.setCity(city.get());

        this.forecastRepository.save(forecast);

        return Messages.SUCCESSFULLY + Messages.FORECAST + Messages.INTERVAL + forecast.importForecast();
    }
}