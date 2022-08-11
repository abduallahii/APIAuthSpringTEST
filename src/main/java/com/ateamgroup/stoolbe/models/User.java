package com.ateamgroup.stoolbe.models;

import com.ateamgroup.stoolbe.repo.UserRoleToPrivilege;
import com.ateamgroup.stoolbe.repo.UserToRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private long id;
    private String userID;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private Date LastLoginDate;
    private Date LastLoginDateDisplay;
    private Date joinDate;

    @OneToMany(mappedBy = "user")
    private List<UserToRole> userToRoles;

    private boolean isActive;
    private boolean isNotLocked;

    public User(String userID,
                String firstName,
                String lastName,
                String username,
                String email,
                String password,
                Date lastLoginDate,
                Date lastLoginDateDisplay,
                Date joinDate,
                List<UserToRole> userToRoles,
                boolean isActive,
                boolean isNotLocked) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        LastLoginDate = lastLoginDate;
        LastLoginDateDisplay = lastLoginDateDisplay;
        this.joinDate = joinDate;
        this.userToRoles = userToRoles;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
    }


    public Hashtable<String, List<String>> getUserToRoles() {
        Hashtable<String, List<String>> auth_dict = new Hashtable<String, List<String>>();
        if (!this.userToRoles.isEmpty()) {
            for (UserToRole r : this.userToRoles) {
                List<String> privllage = new ArrayList<String>();
                for (UserRoleToPrivilege ur : r.getRole().getUserRoleToPrivileges()) {
                    privllage.add(ur.getPrivilege().getPrivilegeName());
                }
                auth_dict.put(r.getRole().getRoleName(), privllage);
            }
        }
        return auth_dict;
    }

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        SimpleGrantedAuthority auth ;
        for (UserToRole userToRole : this.userToRoles) {
            for (UserRoleToPrivilege userRoleToPrivilege : userToRole.getRole().getUserRoleToPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(userToRole.getRole().getRoleName() + ":" + userRoleToPrivilege.getPrivilege().getPrivilegeName()));
//                authorities.add(new SimpleGrantedAuthority());
            }
        }
        return authorities;


    }

}