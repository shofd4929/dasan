package com.example.springbatch.entity3;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class OTPINFO3 {

    @Id
    private int otpcode;
    private Date otpdate;
}
