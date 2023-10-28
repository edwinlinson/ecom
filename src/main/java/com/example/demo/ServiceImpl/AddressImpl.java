package com.example.demo.ServiceImpl;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;
import com.example.demo.Repository.AddressRepo;
import com.example.demo.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressImpl implements AddressService {
    @Autowired
    AddressRepo addressRepo;
    @Override
    public List<Address> getAllAddressesByUser(User user) {
      return  addressRepo.findAllByUser(user);
    }

    @Override
    public Address addAddress(Address address) {
        return addressRepo.save(address);
    }

    @Override
    public Optional<Address> getAddressById(int id) {
        return addressRepo.findById(id);
    }

    @Override
    public void deleteAddressById(int addressId) {
        addressRepo.deleteById(addressId);
    }
}
