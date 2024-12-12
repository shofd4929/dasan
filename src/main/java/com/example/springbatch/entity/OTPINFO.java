package com.example.springbatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class OTPINFO {

    @Id
    private int otpcode;
    private Date otpdate;
}
