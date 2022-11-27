package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.LaptopImportDto;
import exam.model.entity.Laptop;
import exam.model.entity.Shop;
import exam.repository.LaptopRepository;
import exam.repository.ShopRepository;
import exam.service.LaptopService;
import exam.util.Messages;
import exam.util.PathFiles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaptopServiceImpl implements LaptopService {

    private final LaptopRepository laptopRepository;
    private final ShopRepository shopRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public LaptopServiceImpl(
            LaptopRepository laptopRepository,
            ShopRepository shopRepository,
            Gson gson,
            Validator validator,
            ModelMapper mapper) {
        this.laptopRepository = laptopRepository;
        this.shopRepository = shopRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }


    @Override
    public boolean areImported() {
        return this.laptopRepository.count() > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        return Files.readString(PathFiles.LAPTOPS_PATH);
    }

    @Override
    public String importLaptops() throws IOException {
        final String json = this.readLaptopsFileContent();

                final LaptopImportDto[] importLaptops = this.gson.fromJson(json, LaptopImportDto[].class);

                final List<String> result = new ArrayList<>();

                for (LaptopImportDto importLaptop : importLaptops) {

                    final Set<ConstraintViolation<LaptopImportDto>> validationErrors =
                            this.validator.validate(importLaptop);

                    if (validationErrors.isEmpty()) {

                        final Optional<Laptop> laptopExist =
                                this.laptopRepository.findByMacAddress(importLaptop.getMacAddress());

                        boolean canAdded = laptopExist.isEmpty();

                        if (canAdded) {

                            Laptop laptop = this.mapper.map(importLaptop, Laptop.class);

                            Shop shop = this.shopRepository
                                    .findByName(importLaptop.getShop().getName())
                                    .orElseThrow(NoSuchElementException::new);

                            laptop.setShop(shop);

                            this.laptopRepository.save(laptop);

                            final String msg =
                                    Messages.SUCCESSFULLY + Messages.LAPTOP + Messages.INTERVAL + laptop.importInfo();

                            result.add(msg);

                        } else {
                            result.add(Messages.INVALID + Messages.LAPTOP);
                        }

                    } else {
                        result.add(Messages.INVALID + Messages.LAPTOP);
                    }
                }
                return String.join(System.lineSeparator(), result);
    }

    @Override
    public String exportBestLaptops() {
        return this.laptopRepository
                .findAllByOrderByCpuSpeedDescRamDescStorageDescMacAddressAsc()
                .orElseThrow(NoSuchElementException::new)
                .stream()
                .map(Laptop::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
