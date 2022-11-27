package exam.service.impl;

import exam.model.dto.ShopImportDto;
import exam.model.dto.ShopWrapperDto;
import exam.model.entity.Shop;
import exam.model.entity.Town;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final TownRepository townRepository;

    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public ShopServiceImpl(
            ShopRepository shopRepository,
            TownRepository townRepository,
            Validator validator,
            ModelMapper mapper) {
        this.shopRepository = shopRepository;
        this.townRepository = townRepository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.shopRepository.count() > 0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        return Files.readString(PathFiles.SHOPS_PATH);
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {

        final FileReader fileReader = new FileReader(PathFiles.SHOPS_PATH.toFile());

        final JAXBContext context = JAXBContext.newInstance(ShopWrapperDto.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final ShopWrapperDto shopsDto = (ShopWrapperDto) unmarshaller.unmarshal(fileReader);


        return shopsDto
                .getShops()
                .stream()
                .map(this::importShop)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importShop(ShopImportDto dto) {

        Set<ConstraintViolation<ShopImportDto>> errors = this.validator.validate(dto);

        if (!errors.isEmpty()) {
            return Messages.INVALID + Messages.SHOP;
        }

        Optional<Shop> shopExist = this.shopRepository.findByName(dto.getName());

        if (shopExist.isPresent()) {
            return Messages.INVALID + Messages.SHOP;
        }

        Shop shop = this.mapper.map(dto, Shop.class);

        Town town =
                this.townRepository
                        .findByName(dto.getTown().getName())
                        .orElseThrow(NoSuchElementException::new);

        shop.setTown(town);

        this.shopRepository.save(shop);

        return Messages.SUCCESSFULLY + Messages.SHOP + Messages.INTERVAL + shop;
    }
}
