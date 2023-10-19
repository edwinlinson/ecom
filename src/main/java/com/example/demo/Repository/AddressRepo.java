package com.example.demo.Repository;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address, Integer> {

    List<Address> findAllByUser(User user);
}
