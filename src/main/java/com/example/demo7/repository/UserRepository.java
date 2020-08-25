package com.example.demo7.repository;

import com.example.demo7.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    int countByUsername(String username);
}
