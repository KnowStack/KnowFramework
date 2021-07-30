package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.BaseTest;
import com.didiglobal.logi.security.common.vo.record.RecordQueryVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cjm
 * TODO：考虑好怎么创建再测试
 */
public class RecordServiceTest extends BaseTest {

    @Autowired
    private RecordService recordService;

    @Test
    public void testGetPageRecord() {
        RecordQueryVo queryVo = new RecordQueryVo();
        queryVo.setSize(5);
        queryVo.setPage(1);
        recordService.getPageRecord(queryVo);
    }
}
