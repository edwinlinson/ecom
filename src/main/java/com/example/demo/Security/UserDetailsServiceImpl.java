package com.example.demo.Security;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findUserByEmail(username);
//        if (user == null || !user.isEnabled()){
//            System.out.println("Sorry,Account does not exist.");
//            throw new UsernameNotFoundException("Sorry,Account does not exist.");
//        }
        if(user == null ){
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            session.setAttribute("errorMessage", "Sorry, Account does not exist.");
            throw new UsernameNotFoundException("Sorry, Account does not exist.");
        } else if (!user.isEnabled()) {
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            session.setAttribute("errorMessage", "Account is Blocked.");
            throw new UsernameNotFoundException("Account is Blocked.");
        }
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return userDetails;
    }
}
