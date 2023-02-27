package com.TelegramBots.telegrambots.model;

import com.TelegramBots.telegrambots.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findAllByUserId (Long userId);
    Optional<User> findAllByPhone (String phone);


}
