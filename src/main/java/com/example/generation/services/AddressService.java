package com.example.generation.services;

import com.example.generation.entities.Address;
import com.example.generation.repositories.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Address> findAll(){
        return addressRepository.findAll();
    }

    public Optional<Address> findById(Long id){
        return addressRepository.findById(id);
    }

    public Address save(Address address){
        return addressRepository.save(address);
    }

    public Address update(Address address){
        return addressRepository.save(address);
    }

    public void deleteById(Long id){
        addressRepository.deleteById(id);
    }
}
