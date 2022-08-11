package com.ateamgroup.stoolbe.repo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String roleName;
    @OneToMany(mappedBy = "role")
    private List<UserRoleToPrivilege> userRoleToPrivileges;

    public UserRole() {
    }

    public UserRole(String roleName, List<UserRoleToPrivilege> userRoleToPrivileges) {
        this.roleName = roleName;
        this.userRoleToPrivileges = userRoleToPrivileges;
    }

}