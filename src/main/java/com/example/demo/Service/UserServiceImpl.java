package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Model.ShoppingCart;
import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepo;
import com.example.demo.ServiceImpl.ShoppingCartServiceImpl;
import com.example.demo.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserServiceInterface {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    ProductService productService;

    @Override
    public void saveUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getFirstname()+ " " + userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("user");
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEnabled(true);
        String code = generateCode();
        user.setReferralCode(code);
        userRepo.save(user);
    }

    private String generateCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    public void saveUserAdmin(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getFirstname()+ " " + userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEnabled(true);
        userRepo.save(user);
    }
    @Override
    public User updateUser(UserDTO user) {
        int id = user.getId();
        User existing = userRepo.findUserById(id);
        existing.setName(user.getFirstname()+ " " +user.getLastname());
        existing.setRole(user.getRole());
        existing.setEmail(user.getEmail());
        existing.setPassword(passwordEncoder.encode((user.getPassword())));
        existing.setRole(user.getRole());
        existing.setEnabled(user.isEnabled());
        return userRepo.save(existing);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }
    @Override
    public UserDTO findById(int id) {
        User existing = userRepo.findUserById(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(existing.getId());
        String [] name = existing.getName().split(" ");
        userDTO.setFirstname(name[0]);
        userDTO.setLastname(name[1]);
        userDTO.setEmail(existing.getEmail());
        userDTO.setPassword(existing.getPassword());
        userDTO.setRole(existing.getRole());
        userDTO.setPhoneNumber(existing.getPhoneNumber());
        userDTO.setEnabled(existing.isEnabled());
        return userDTO;
    }
    public User finById(int id){
        return userRepo.findUserById(id);
    }

    @Override
    public List<UserDTO> findAllByRole(String role) {
        List<User> users =userRepo.getAllByRole(role);
        return users.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public UserDTO convertEntityToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        String [] name = user.getName().split(" ");
        userDTO.setFirstname(name[0]);
        userDTO.setLastname(name[1]);
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setEnabled(user.isEnabled());
        return userDTO;
    }

    public User findUserByUsername(String username){
        return userRepo.findUserByEmail(username);
    }

    @Override
    public void deleteUserById(int id) {
        userRepo.deleteById(id);
    }
    public void disableUserById(int id){
        User user =userRepo.findUserById(id);
        if (user !=null ){
            user.setEnabled(!user.isEnabled());
            userRepo.save(user);
        }
    }

    @Override
    public List<UserDTO> searchUser(String role, String name) {
        return null;
    }

    public  List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void addToWishList(String name, long id) {
        User user = findUserByUsername(name);
        Optional<Product> optionalProduct = productService.getProductById(id);
        if (user != null && optionalProduct.isPresent()){
            List<Product> wishList = user.getWishlist();
            Product product= optionalProduct.get();
            wishList.add(product);
            userRepo.save(user);
        }else {
            throw new UsernameNotFoundException("Product not found");
        }
    }

    public List<Product> getWishList(String name) {
        User user = findUserByUsername(name);
        return user.getWishlist();
    }

    public void clearWishList(String name) {
        User user = findUserByUsername(name);
        user.getWishlist().clear();
        userRepo.save(user);
    }

    public void updateWishlist(String name, List<Product> wishList) {
        User user = userRepo.findUserByEmail(name);
        user.setWishlist(wishList);
        userRepo.save(user);
    }

//    public void addWtoCart(String name, long id) {
//        User user = findUserByUsername(name);
//        ShoppingCart cart= user.getCart();
//        if (cart == null){
//            cart = new ShoppingCart();
//            cart.setUser(user);
//        }
//        Product product = productService.getProductById(id).get();
//        cartService.addItemToCart(product,1,name);
//    }
}
