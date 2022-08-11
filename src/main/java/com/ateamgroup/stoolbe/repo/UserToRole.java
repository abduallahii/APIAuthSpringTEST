package com.ateamgroup.stoolbe.repo;

import com.ateamgroup.stoolbe.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserToRole {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private User user;
    @ManyToOne
    private UserRole role;



    public UserToRole(User user, UserRole role) {
        this.user = user;
        this.role = role;
    }
}