package kr.go.ebankingBatch.entity;

import jakarta.persistence.*;
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

    @Temporal(TemporalType.DATE)
    private Date otpdate;
}
