package com.example.springbatch.entity2;

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
    private Long otpcode;
    private Date otpdate;
}
