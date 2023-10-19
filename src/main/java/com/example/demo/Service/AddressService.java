package com.example.demo.Service;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;

import java.util.List;

public interface AddressService {
    List<Address> getAllAddressesByUser(User user);
    Address addAddress(Address address);

    void deleteAddressById(int addressId);
}
