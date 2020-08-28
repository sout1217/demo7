package com.example.demo7.service;

import com.example.demo7.domain.dto.AccountDto;
import com.example.demo7.domain.entity.Account;

import java.util.List;

public interface UserService {

    void createUser(Account account);

    void modifyUser(AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void deleteUser(Long id);

    void order();
}
