package kr.go.ebankingBatch;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;

@Slf4j
public class JasyptTest {

    @Test
    public void jasyptTest(){
        String value = "HANAITCPS";
        String result = jasyptEncoding(value);
        log.info(result);
    }

    public String jasyptEncoding(String value){
        String key = "KScjy3pKDd8v0LYXs2qGqS9H0NrF0iL/9TBol4RKFeY1ciek43GIbReHC2uvshSd";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWITHMD5ANDDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
