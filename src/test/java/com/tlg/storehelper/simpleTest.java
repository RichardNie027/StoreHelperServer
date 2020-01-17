package com.tlg.storehelper;

import com.nec.lib.utils.Base64Util;
import org.junit.Test;

public class simpleTest {

    @Test
    public void testRegex() throws Exception {
        String str = "1*34#*5678==";
        System.out.println(str.replaceAll("[#*]", "_"));
        System.out.println(str.replaceAll("=", ""));
    }

    @Test
    public void testBase64() throws Exception {
        System.out.println(Base64Util.encode("2"));
        System.out.println(Base64Util.encode("0"));
        System.out.println(Base64Util.decode("MA"));
        System.out.println(Base64Util.encode("LANCY00.jpg|201912161537", true));
        System.out.println(Base64Util.decode("TEFOQ1kwMC5qcGd8MjAxOTEyMTYxNTM3"));
    }

}
