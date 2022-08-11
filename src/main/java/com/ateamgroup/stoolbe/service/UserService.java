package com.ateamgroup.stoolbe.service;

import com.ateamgroup.stoolbe.exceptions.EmailExistException;
import com.ateamgroup.stoolbe.exceptions.EmailNotFoundException;
import com.ateamgroup.stoolbe.exceptions.UsernameExistException;
import com.ateamgroup.stoolbe.models.User;

import java.util.List;

public interface UserService {
    User register(String firstname , String lastName , String username , String email) throws EmailExistException, UsernameExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addUser(String firstname , String lastName , String username , String email ,  boolean isNonLocked , boolean isActive) throws EmailExistException, UsernameExistException;

    User updateUser( String currentUsername , String newfirstname , String newlastName , String newusername , String newemail ,boolean isNonLocked , boolean isActive) throws EmailExistException, UsernameExistException;

    void deleteUser (long id) ;

    void resetPassword (String email) throws EmailNotFoundException;


}
