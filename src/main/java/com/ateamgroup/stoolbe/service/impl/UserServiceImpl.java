package com.ateamgroup.stoolbe.service.impl;

import com.ateamgroup.stoolbe.models.User;
import com.ateamgroup.stoolbe.models.UserPrincipal;
import com.ateamgroup.stoolbe.repo.RolesRepository;
import com.ateamgroup.stoolbe.repo.UserRepository;
import com.ateamgroup.stoolbe.repo.UserToRole;
import com.ateamgroup.stoolbe.exceptions.EmailExistException;
import com.ateamgroup.stoolbe.exceptions.EmailNotFoundException;
import com.ateamgroup.stoolbe.exceptions.UsernameExistException;
import com.ateamgroup.stoolbe.repo.UserToRolesRepository;
import com.ateamgroup.stoolbe.service.EmailService;
import com.ateamgroup.stoolbe.service.LoginAttemptService;
import com.ateamgroup.stoolbe.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.ateamgroup.stoolbe.constant.UserImplConstant.*;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {


    private UserRepository userRepository;

    private UserToRolesRepository userToRolesRepository;
    private RolesRepository rolesRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserToRolesRepository userToRolesRepository, RolesRepository rolesRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, EmailService emailService) {
        this.userRepository = userRepository;
        this.userToRolesRepository = userToRolesRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            logger.error("User Name :: " + username + " Not Found ");
            throw new UsernameNotFoundException(NO_USER_NAME_FOUND);
        } else {
            try {
                validateLoginAttempt(user);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            logger.info(FOUND_USER + username);
            return userPrincipal;
        }
    }


    @Override
    public User register(String firstname, String lastName, String username, String email) throws UsernameNotFoundException, EmailExistException, UsernameExistException {
        validateNewUsernameAndEmdail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserID(generateUSerId());
        String password = generatePassword();
        user.setFirstName(firstname);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodeePassword(password));
        user.setActive(false);
        user.setNotLocked(true);
//        UserToRole mainRole = new UserToRole(user,rolesRepository.getById(0));
//        UserToRole mainRole = new UserToRole(user,rolesRepository.getById(1));
//        List<UserToRole> userToRoles = List.of(mainRole);
        user.setUserToRoles(defaultRolesList(user));
        userRepository.save(user);
        logger.info("New User Password : " + password);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User addUser(String firstname, String lastName, String username, String email, boolean isNonLocked, boolean isActive) throws EmailExistException, UsernameExistException {
        validateNewUsernameAndEmdail(StringUtils.EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserID(generateUSerId());
        user.setFirstName(firstname);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodeePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setUserToRoles(defaultRolesList(user));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newfirstname, String newlastName, String newusername, String newemail,  boolean isNonLocked, boolean isActive) throws EmailExistException, UsernameExistException {
        User currentUser =  validateNewUsernameAndEmdail(currentUsername, newusername, newemail);
        currentUser.setFirstName(newfirstname);
        currentUser.setLastName(newlastName);
        currentUser.setJoinDate(new Date());
        currentUser.setUsername(newusername);
        currentUser.setEmail(newemail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        userRepository.save(currentUser);
        return currentUser ;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email) ;
        }
        String password = generatePassword();
        String encodePassword = encodeePassword(password);
        user.setPassword(encodePassword);
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    private String encodeePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUSerId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private List<UserToRole> defaultRolesList(User user) {
        UserToRole mainRole = new UserToRole(user, rolesRepository.getById(1));
        userToRolesRepository.save(mainRole);
        return List.of(mainRole);
    }

    private void validateLoginAttempt(User user) throws ExecutionException {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }


    private User validateNewUsernameAndEmdail(String currentUsername, String newUsername, String Email)
            throws UsernameNotFoundException,
            UsernameExistException,
            EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(Email);
        logger.info("Validate username and Email ::START::");
        if (StringUtils.isNoneBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);

            if (currentUser == null) {
                throw new UsernameNotFoundException(NO_USER_NAME_FOUND + currentUsername);
            }

            if (userByNewUsername != null && currentUser.getId() != userByNewUsername.getId()) {
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if (userByNewEmail != null && currentUser.getId() != userByNewEmail.getId()) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            logger.info("Validate username and Email ::END::");
            return null;
        }

    }
}
