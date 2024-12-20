package kr.go.ebankingBatch;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;

public class JasyptTest {

    @Test
    public void jasyptTest(){
        String value = "1111";
        String result = jasyptEncoding(value);
        System.out.println(result);
    }

    public String jasyptEncoding(String value){
        String key = "KScjy3pKDd8v0LYXs2qGqS9H0NrF0iL/9TBol4RKFeY1ciek43GIbReHC2uvshSd";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWITHMD5ANDDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
