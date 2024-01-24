package com.project.storage.storage.service;


import com.project.storage.storage.entity.Role;
import com.project.storage.storage.entity.Store;
import com.project.storage.storage.entity.User;
import com.project.storage.storage.repository.RoleRepository;
import com.project.storage.storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private StoreService storeService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
   // ακροατης που δημιουργει την 1η μονο φορα χρηστη στην ΒΔ
    @EventListener(ApplicationReadyEvent.class)
    public User createAdmin() {
        if (userRepository.findByUsername("admin") == null) {


            User admin = new User("admin", "admin", "admin", passwordEncoder.encode("Admin!1234"), "admin", 1,
                    roleRepository.findByName("SuperAdmin"),storeService.findById(1));
            return userRepository.save(admin);
        }

        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User authenticatedUser = userRepository.findByUsername(username);
        //this.authenticatedUser = authenticatedUser;
        if (authenticatedUser == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(authenticatedUser.getUsername(), authenticatedUser.getPassword(), getAuthorities(authenticatedUser.getRoleByUserrole()));
    }


    public Collection<? extends GrantedAuthority> getAuthorities(Role role) {

        return Arrays.asList(new SimpleGrantedAuthority(role.getName()));
    }


    @Override
    public User save(User user) {

        String lastname = user.getLastname().trim();
        String firstname = user.getFirstname().trim();
        String username = user.getUsername().trim();
        String code = passwordEncoder.encode(user.getPassword());
        Role userRole = userRoleService.findById(user.getRoleByUserrole().getId());
        String comments = user.getComments();
        Store store = storeService.findById(user.getStoreByStoreId().getId());

        if (userRepository.findByUsername(username) == null) {

            User newUser = new User(comments, lastname, firstname, code, username, 1,
                    userRole,store);
            return userRepository.save(newUser);

        } else return null;



    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void edit(int id, User user , String newPassword) {
        User found = userRepository.findById(id);

        found.setComments(user.getComments());
        found.setFirstname(user.getFirstname());
        found.setLastname(user.getLastname());
        found.setRoleByUserrole(user.getRoleByUserrole());
        found.setStoreByStoreId(user.getStoreByStoreId());
        found.setEnable(user.getEnable());

        if (   newPassword !=""){

            found.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(found);
    }




    @Override
    public void changepass(int id, User user) {
        User found = userRepository.findById(id);
        found.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(found);
    }

    @Override
    public void disable(User user) {

        user.setEnable(0);
        userRepository.save(user);
    }

    @Override
    public void enable(User user) {
        user.setEnable(1);
        userRepository.save(user);
    }

    // μεθοδος που επιστρεφει τυχαια συμβολοσειρα
    public static String alphaNumericString(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
