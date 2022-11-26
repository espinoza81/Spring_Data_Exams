package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CountryImportDto;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
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
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public CountryServiceImpl(
            CountryRepository countryRepository,
            Gson gson, Validator validator,
            ModelMapper mapper) {
        this.countryRepository = countryRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(PathFiles.COUNTRIES_PATH);
    }

    @Override
    public String importCountries() throws IOException {
        final String json = this.readCountriesFromFile();

        final CountryImportDto[] importCountries = this.gson.fromJson(json, CountryImportDto[].class);

        final List<String> result = new ArrayList<>();

        for (CountryImportDto importCountry : importCountries) {

            final Set<ConstraintViolation<CountryImportDto>> validationErrors =
                    this.validator.validate(importCountry);

            if (validationErrors.isEmpty()) {

                final Optional<Country> countryExist = this.countryRepository.findByCountryName(importCountry.getCountryName());

                if(countryExist.isEmpty()) {

                    Country country = this.mapper.map(importCountry, Country.class);

                    this.countryRepository.save(country);

                    final String msg = Messages.SUCCESSFULLY + Messages.COUNTRY + Messages.INTERVAL + country;

                    result.add(msg);

                } else {
                    result.add(Messages.INVALID + Messages.COUNTRY);
                }

            } else {
                result.add(Messages.INVALID + Messages.COUNTRY);
            }
        }
        return String.join(System.lineSeparator(), result);
    }
}