package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.dto.UserDTO;

import java.util.List;

public interface UserServiceInterface {
  void saveUser(UserDTO userDTO);
    User findByEmail(String email);

    UserDTO findById(int id);

    List<UserDTO> findAllByRole(String role);

    User updateUser(UserDTO user);

    void deleteUserById(int id);
    List<UserDTO> searchUser(String role,String name);

    void addToUserBalance(User user, double orderAmount);
}
