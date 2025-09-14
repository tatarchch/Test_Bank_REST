package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByRole(String role);

    Optional<User> findUserByUsernameAndRole(String username, String role);

    Optional<User> findUserByUsername(String username);

    Boolean existsUserByUsername(String username);

}
