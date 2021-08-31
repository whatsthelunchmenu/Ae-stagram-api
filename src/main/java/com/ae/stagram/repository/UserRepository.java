package com.ae.stagram.repository;

import com.ae.stagram.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);
}
