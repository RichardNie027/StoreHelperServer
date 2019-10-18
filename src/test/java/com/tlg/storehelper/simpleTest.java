package com.tlg.storehelper;

import org.junit.Test;

public class simpleTest {

    @Test
    public void testRegex() throws Exception {
        String str = "1*34#*5678";
        System.out.println(str.replaceAll("[#*]", "_"));
    }
}
