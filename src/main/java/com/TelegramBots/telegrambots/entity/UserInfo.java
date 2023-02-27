package com.TelegramBots.telegrambots.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Long userId;

    private String firstName;
    private String lastName;
    private String nicName;
    private String phone;


    @Override
    public String toString() {
        return "\n✅Пользователь найден\n" + "\uD83C\uDD94: " + this.userId.toString() + "\n\uD83D\uDCF1: "
                + this.phone + "\nNicName: "
                + this.nicName + "\nFirstName: "
                + this.firstName + "\nLastName: "
                + this.lastName + "\n";
    }
}
