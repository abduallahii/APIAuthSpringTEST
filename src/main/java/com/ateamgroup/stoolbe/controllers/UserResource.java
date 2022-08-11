package com.ateamgroup.stoolbe.controllers;

import com.ateamgroup.stoolbe.exceptions.*;
import com.ateamgroup.stoolbe.models.User;
import com.ateamgroup.stoolbe.models.UserPrincipal;
import com.ateamgroup.stoolbe.utility.JWTTokenProvider;
import com.ateamgroup.stoolbe.exceptions.*;
import com.ateamgroup.stoolbe.models.HttpResponse;
import com.ateamgroup.stoolbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ateamgroup.stoolbe.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(value = "/user")
public class UserResource extends ExceptionHandlerController {
    private UserService userService ;
    private AuthenticationManager authenticationManager ;
    private JWTTokenProvider jwtTokenProvider ;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }




    @PostMapping("/register")
    public ResponseEntity<User> Register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User userObj = userService.register(user.getFirstName(),user.getLastName(),  user.getUsername(), user.getEmail() );
        return new ResponseEntity<>(userObj, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> Login(@RequestBody User user) {
        authenticate(user.getUsername(),user.getPassword());
        User userObj = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(userObj);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(userObj, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<User> addUser(@RequestParam("firstName") String firstName ,
                                        @RequestParam("lastName") String lastName ,
                                        @RequestParam("userName") String userName ,
                                        @RequestParam("email") String email ,
                                        @RequestParam("isActive") String isActive ,
                                        @RequestParam("isNonLocked") String isNonLocked     ) throws EmailExistException, UsernameExistException {
        User newUser = userService.addUser(firstName,lastName,userName,email, Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive));
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }




@PostMapping("/update")
public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername ,
                                       @RequestParam("firstName") String firstName ,
                                    @RequestParam("lastName") String lastName ,
                                    @RequestParam("userName") String userName ,
                                    @RequestParam("email") String email ,
                                    @RequestParam("isActive") String isActive ,
                                    @RequestParam("isNonLocked") String isNonLocked     ) throws EmailExistException, UsernameExistException {
    User updatedUser = userService.updateUser(currentUsername ,firstName, lastName, userName, email, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive));
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
}

@GetMapping("/find/{username}")
public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
    return new ResponseEntity<>(user, HttpStatus.OK);
}


    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> getAllUsers(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return  response(HttpStatus.OK , "Email was sent to : " + email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('USERSMANAGMENT:DELETEUSER')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return  response(HttpStatus.OK , "User Deleted .. " );
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus , String message){
       HttpResponse body = new HttpResponse(
               httpStatus.value(),
               httpStatus,
               httpStatus.getReasonPhrase().toUpperCase() ,
               message.toUpperCase() ) ;
        return new ResponseEntity<>( body ,httpStatus);
    }


    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers ;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

}
