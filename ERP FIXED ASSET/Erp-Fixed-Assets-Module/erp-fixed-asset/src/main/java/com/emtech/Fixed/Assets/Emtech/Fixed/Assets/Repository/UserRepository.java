package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
