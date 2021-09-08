package com.didiglobal.logi.security;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cjm
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SecurityApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

    @Test
    public void baseTest() {
        Assert.assertNull(null);
    }
}
