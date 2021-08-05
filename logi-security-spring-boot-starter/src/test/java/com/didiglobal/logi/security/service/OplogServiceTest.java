package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cjm
 * TODO：考虑好怎么创建再测试
 */
public class OplogServiceTest extends BaseTest {

    @Autowired
    private OplogService oplogService;

    @Test
    public void testGetPageOplog() {
        OplogQueryVo queryVo = new OplogQueryVo();
        queryVo.setSize(5);
        queryVo.setPage(1);
        // oplogService.getRecordPage(queryVo);
    }
}
