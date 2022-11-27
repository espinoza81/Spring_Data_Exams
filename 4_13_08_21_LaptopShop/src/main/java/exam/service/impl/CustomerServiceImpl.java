package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.CustomerImportDto;
import exam.model.entity.Customer;
import exam.model.entity.Town;
import exam.repository.CustomerRepository;
import exam.repository.TownRepository;
import exam.service.CustomerService;
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

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final TownRepository townRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            TownRepository townRepository,
            Gson gson,
            Validator validator,
            ModelMapper mapper) {
        this.customerRepository = customerRepository;
        this.townRepository = townRepository;
        this.gson = gson;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public boolean areImported() {
        return this.customerRepository.count() > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        return Files.readString(PathFiles.CUSTOMERS_PATH);
    }

    @Override
    public String importCustomers() throws IOException {
        final String json = this.readCustomersFileContent();

        final CustomerImportDto[] importCustomers = this.gson.fromJson(json, CustomerImportDto[].class);

        final List<String> result = new ArrayList<>();

        for (CustomerImportDto importCustomer : importCustomers) {

            final Set<ConstraintViolation<CustomerImportDto>> validationErrors =
                    this.validator.validate(importCustomer);

            if (validationErrors.isEmpty()) {

                final Optional<Customer> customerExist = this.customerRepository.findByEmail(importCustomer.getEmail());

                boolean canAdded = customerExist.isEmpty();

                if (canAdded) {

                    Customer customer = this.mapper.map(importCustomer, Customer.class);

                    Town town = this.townRepository
                            .findByName(importCustomer.getTown().getName())
                            .orElseThrow(NoSuchElementException::new);

                    customer.setTown(town);

                    this.customerRepository.save(customer);

                    final String msg = Messages.SUCCESSFULLY + Messages.CUSTOMER + Messages.INTERVAL + customer;

                    result.add(msg);

                } else {
                    result.add(Messages.INVALID + Messages.CUSTOMER);
                }

            } else {
                result.add(Messages.INVALID + Messages.CUSTOMER);
            }
        }
        return String.join(System.lineSeparator(), result);
    }
}
