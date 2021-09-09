package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.dto.oplog.OplogQueryDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cjm
 * TODO：考虑好怎么创建再测试
 */
public class OplogPOServiceTest extends BaseTest {

    @Autowired
    private OplogService oplogService;

    @Test
    public void testGetPageOplog() {
        Assert.assertNull(null);
        OplogQueryDTO queryVo = new OplogQueryDTO();
        queryVo.setSize(5);
        queryVo.setPage(1);
        // oplogService.getRecordPage(queryVo);
    }
}
