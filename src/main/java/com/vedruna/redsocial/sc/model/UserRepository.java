package com.vedruna.redsocial.sc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SecurityUser, Long> {
    Optional<SecurityUser> findByUsername(String username);

}
