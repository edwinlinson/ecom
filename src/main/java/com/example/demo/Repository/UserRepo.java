package com.example.demo.Repository;

import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {



    User findUserByEmail(String email);

    User findUserById(int id);

    List<User> getAllByRole(String role);


    Optional<User> findUserByName(String username);
}
