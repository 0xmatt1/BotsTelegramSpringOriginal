package com.TelegramBots.telegrambots.service;

import com.TelegramBots.telegrambots.entity.Accounts;
import com.TelegramBots.telegrambots.entity.User;
import com.TelegramBots.telegrambots.entity.UserInfo;
import com.TelegramBots.telegrambots.model.AccountRepository;
import com.TelegramBots.telegrambots.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private final UserRepository userRepository;
    @Autowired
    public SearchService( UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public User findFromId(Long id){
        Optional<User>findByUserId = userRepository.findById(id);
        return findByUserId.orElse(null);
    }

    public User findFromPhone(String phone){
        Optional<User> findByPhone = userRepository.findAllByPhone(phone);

        return findByPhone.orElse(null);
    }


}
