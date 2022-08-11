package com.ateamgroup.stoolbe.repo;

import com.ateamgroup.stoolbe.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u From User u where lower(u.username) = lower(:username)")
    User findUserByUsername(@Param("username") String username);
    User findUserByEmail(String email);


}
