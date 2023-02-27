package com.TelegramBots.telegrambots.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Setter
@Getter
@Entity(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Accounts {
    @Id
    @Column(name = "user_id")
    Long userId;
    @Column(name = "name")
    String nameAccounts;


}
