package com.TelegramBots.telegrambots.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NotNull
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usertelegramdb")
public class User {
    @Id
    @Column(name = "User_id")
    private Long userId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "Last_name")
    private String lastName;
    @Column(name = "Nic_name")
    private String nicName;
    @Column(name = "Phone")
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
