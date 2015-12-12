package com.drownedinsound.test;

import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.DisWebPagerParserImpl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class DisBoardWebParserTest {

    private DisWebPageParser disWebPageParser;


    @Before
    public void setup(){
        disWebPageParser = new DisWebPagerParserImpl(null,null);
    }

    @Test
    public void testGetAuthToken() throws Exception{
        String expected = "uVzOJW0EFRZnB/atMD+vo9Ead2N15LSw+LBQ0tztJDA=";
        URL url = getClass().getResource("auth_token.html");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("auth_token.html");
        String auctal = disWebPageParser.getAuthenticationToken(inputStream);
        Assert.assertEquals(expected,auctal);
    }

}
