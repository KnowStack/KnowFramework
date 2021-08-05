package com.didiglobal.logi.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.security.common.PagingData;
import com.didiglobal.logi.security.common.dto.OplogDto;
import com.didiglobal.logi.security.common.entity.Oplog;
import com.didiglobal.logi.security.common.vo.oplog.OplogQueryVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogTypeVo;
import com.didiglobal.logi.security.common.vo.oplog.OplogVo;
import com.didiglobal.logi.security.common.vo.oplog.TargetTypeVo;
import com.didiglobal.logi.security.mapper.OplogMapper;
import com.didiglobal.logi.security.service.OplogService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author cjm
 */
@Service
public class OplogServiceImpl implements OplogService {

    @Autowired
    private OplogMapper oplogMapper;

    @Override
    public PagingData<OplogVo> getOplogPage(OplogQueryVo queryVo) {
        QueryWrapper<Oplog> queryWrapper = new QueryWrapper<>();
        // 分页查询
        IPage<Oplog> oplogPage = new Page<>(queryVo.getPage(), queryVo.getSize());
        // 不查找detail字段
        queryWrapper.select(oplog -> !"detail".equals(oplog.getColumn()));
        /*
        queryWrapper
                .eq(queryVo.getRecordType() != null, "operate_type", queryVo.getRecordType())
                .eq(queryVo.getTargetType() != null, "target_type", queryVo.getTargetType())
                .ge(queryVo.getStartTime() != null, "create_time", queryVo.getStartTime())
                .le(queryVo.getEndTime() != null, "create_time", queryVo.getEndTime())
                .like(queryVo.getTarget() != null, "target", queryVo.getTarget())
                .like(queryVo.getRecordIp() != null, "operator_ip", queryVo.getRecordIp())
                .like(queryVo.getRecordUsername() != null, "operator_username", queryVo.getRecordUsername());
        */
        oplogMapper.selectPage(oplogPage, queryWrapper);
        // 转成vo
        List<OplogVo> oplogVoList = CopyBeanUtil.copyList(oplogPage.getRecords(), OplogVo.class);

        return new PagingData<>(oplogVoList, oplogPage);
    }

    @Override
    public OplogVo getDetailById(Integer oplogId) {
        return null;
    }

    @Override
    public List<OplogTypeVo> getOperateTypeList() {
        return null;
    }

    @Override
    public List<TargetTypeVo> getTargetTypeList() {
        return null;
    }

    @Override
    public void saveOplog(OplogDto oplogDto) {
        RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        // 获取客户端真实ip地址
        String realIpAddress = getRealIpAddress(request);
        Oplog oplog = CopyBeanUtil.copy(oplogDto, Oplog.class);
        oplog.setOperatorIp(realIpAddress);
        // TODO 这里要通过token获取操作者信息
        oplog.setOperatorUsername("testUsername");
        oplogMapper.insert(oplog);
    }

    public String getRealIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localIp = "127.0.0.1";
            String localIpv6 = "0:0:0:0:0:0:0:1";
            if (ipAddress.equals(localIp) || ipAddress.equals(localIpv6)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
        String ipSeparate = ",";
        int ipLength = 15;
        if (ipAddress != null && ipAddress.length() > ipLength) {
            if (ipAddress.indexOf(ipSeparate) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(ipSeparate));
            }
        }
        return ipAddress;
    }
}
