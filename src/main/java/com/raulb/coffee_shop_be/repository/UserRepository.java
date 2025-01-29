package com.raulb.coffee_shop_be.repository;

import com.raulb.coffee_shop_be.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
