package com.tlg.storehelper;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.Test;

public class JasyptTest {

    @Test
    public void testEncrypt() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");          // 加密的算法，这个算法是默认的
        config.setPassword("tlg_it");                        // 加密的密钥
        standardPBEStringEncryptor.setConfig(config);
        String plainText;
        plainText = "*Mis2011#";
        plainText = "123458xiao";
        plainText = "tlgfs";
        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);
        System.out.println(encryptedText);
    }

    @Test
    public void testDecrypt() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("tlg_it");
        standardPBEStringEncryptor.setConfig(config);
        String encryptedText = "jkjsCejtTMsYq7ccwoB2jIzgjfvz5L/8";
        String plainText = standardPBEStringEncryptor.decrypt(encryptedText);
        System.out.println(plainText);
    }

}
