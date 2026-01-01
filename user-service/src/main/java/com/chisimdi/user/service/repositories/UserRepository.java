package com.chisimdi.user.service.repositories;

import com.chisimdi.user.service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUserName(String userName);
    List<User>findByRoles(String roles);
}
