package com.example.demo7.service.impl;

import com.example.demo7.domain.Account;
import com.example.demo7.repository.UserRepository;
import com.example.demo7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(Account account) {

        userRepository.save(account);

    }
}
