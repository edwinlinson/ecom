package com.example.demo.Service;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    List<Address> getAllAddressesByUser(User user);
    Address addAddress(Address address);

    Optional<Address> getAddressById(int id);

    void deleteAddressById(int addressId);
}
