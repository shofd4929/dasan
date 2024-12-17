package kr.go.ebankingBatch.entity2;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class OTPINFO2 {

    @Id
    private int otpcode;
    private Date otpdate;
}
