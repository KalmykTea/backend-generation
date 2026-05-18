package com.example.generation.config;

import com.example.generation.entities.Address;
import com.example.generation.entities.User;
import com.example.generation.enums.Role;
import com.example.generation.enums.UserStatus;
import com.example.generation.repositories.AddressRepository;
import com.example.generation.repositories.UserRepository;
import com.example.generation.services.AccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final AddressRepository addressRepository;

    public DataSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AccountService accountService,
            AddressRepository addressRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
        this.addressRepository = addressRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        Address employeeAddress = new Address();
        employeeAddress.setAddressLine("Hoofdstraat 1");
        employeeAddress.setPostalCode("2012AB");
        employeeAddress.setCity("Haarlem");
        employeeAddress.setCountry("Netherlands");
        addressRepository.save(employeeAddress);

        User employee = new User();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("employee@test.com");
        employee.setPassword(passwordEncoder.encode("password123"));
        employee.setBsnNumber("123456789");
        employee.setBirthdate(LocalDate.of(1990, 1, 1));
        employee.setPhoneNumber("0612345678");
        employee.setRole(Role.EMPLOYEE);
        employee.setUserStatus(UserStatus.APPROVED);
        employee.setAddress(employeeAddress);
        userRepository.save(employee);
        accountService.createAccountsForUser(employee);

        Address customerAddress = new Address();
        customerAddress.setAddressLine("Kalverstraat 10");
        customerAddress.setPostalCode("1012NX");
        customerAddress.setCity("Amsterdam");
        customerAddress.setCountry("Netherlands");
        addressRepository.save(customerAddress);

        User customer = new User();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setEmail("customer@test.com");
        customer.setPassword(passwordEncoder.encode("password123"));
        customer.setBsnNumber("987654321");
        customer.setBirthdate(LocalDate.of(1995, 5, 15));
        customer.setPhoneNumber("0698765432");
        customer.setRole(Role.CUSTOMER);
        customer.setUserStatus(UserStatus.APPROVED);
        customer.setAddress(customerAddress);
        userRepository.save(customer);
        accountService.createAccountsForUser(customer);
    }
}