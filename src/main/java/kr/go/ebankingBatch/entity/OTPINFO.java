package kr.go.ebankingBatch.entity;

import jakarta.persistence.Column;
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
    @Column(name = "otpcode")
    private int id;

    private Date otpdate;
}
