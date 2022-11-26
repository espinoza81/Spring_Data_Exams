package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CityImportDto;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;
import softuni.exam.util.Messages;
import softuni.exam.util.PathFiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public CityServiceImpl(
            CityRepository cityRepository,
            CountryRepository countryRepository, Gson gson,
            Validator validator,
            ModelMapper mapper) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.cityRepository.count() > 0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(PathFiles.CITIES_PATH);
    }

    @Override
    public String importCities() throws IOException {
        final String json = this.readCitiesFileContent();

        final CityImportDto[] importCities = this.gson.fromJson(json, CityImportDto[].class);

        final List<String> result = new ArrayList<>();

        for (CityImportDto importCity : importCities) {

            final Set<ConstraintViolation<CityImportDto>> validationErrors =
                    this.validator.validate(importCity);

            if (validationErrors.isEmpty()) {

                final Optional<City> cityExist = this.cityRepository.findByCityName(importCity.getCityName());

                final Optional<Country> country = this.countryRepository.findById(importCity.getCountry());

                if(cityExist.isEmpty() && country.isPresent()) {

                    City city = this.mapper.map(importCity, City.class);

                    city.setCountry(country.get());

                    this.cityRepository.save(city);

                    final String msg = Messages.SUCCESSFULLY + Messages.CITY + Messages.INTERVAL + city;

                    result.add(msg);

                } else {
                    result.add(Messages.INVALID + Messages.CITY);
                }

            } else {
                result.add(Messages.INVALID + Messages.CITY);
            }
        }
        return String.join(System.lineSeparator(), result);
    }
}