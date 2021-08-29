package com.didiglobal.logi.security.service.impl;

import com.didiglobal.logi.security.common.dto.dept.DeptDTO;
import com.didiglobal.logi.security.common.entity.dept.Dept;
import com.didiglobal.logi.security.common.entity.dept.DeptBrief;
import com.didiglobal.logi.security.common.vo.dept.DeptBriefVO;

import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.dept.DeptTreeVO;
import com.didiglobal.logi.security.dao.DeptDao;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.service.DeptService;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author cjm
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;

    @Override
    public DeptTreeVO buildDeptTree() {
        // 获取全部部门并根据level小到大排序
        List<Dept> deptList = deptDao.selectAllAndAscOrderByLevel();
        // 创建一个虚拟根节点
        DeptTreeVO root = DeptTreeVO
                .builder().leaf(false).id(0).childList(new ArrayList<>()).build();

        // 转成树
        Map<Integer, DeptTreeVO> parentMap = new HashMap<>();
        parentMap.put(0, root);
        for(Dept dept : deptList) {
            DeptTreeVO deptTreeVO = CopyBeanUtil.copy(dept, DeptTreeVO.class);
            if(!deptTreeVO.getLeaf()) {
                deptTreeVO.setChildList(new ArrayList<>());
            }
            DeptTreeVO parent = parentMap.get(dept.getParentId());
            if (parent == null) {
                // 如果parent为null，则需要查看下数据库部门表的数据是否有误
                // 1.可能出现了本来该是父节点的节点（有其他子节点的parent为它），但该节点parent为其他子节点的情况（数据异常）
                // 2.也可能是level填写错了（因为前面根据level大小排序）
                throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
            }
            parent.getChildList().add(deptTreeVO);
            parentMap.put(deptTreeVO.getId(), deptTreeVO);
        }
        return root;
    }

    @Override
    public List<DeptBriefVO> getDeptBriefListByChildId(Integer deptId) {
        // 提前获取所有部门
        Map<Integer, Dept> deptMap = getAllDeptMap();
        return getDeptBriefListFromDeptMapByChildId(deptMap, deptId);
    }

    @Override
    public List<Integer> getDeptIdListByParentId(Integer deptId) {
        if(deptId == null) {
            // 如果为null，则获取全部部门的id
            return deptDao.selectAllDeptIdList();
        }
        Set<Integer> deptIdSet = new HashSet<>();
        try {
            getChildDeptIdListByParentId(deptIdSet, deptId);
            return new ArrayList<>(deptIdSet);
        } catch (LogiSecurityException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Integer> getDeptIdListByParentIdAndDeptName(Integer deptId, String deptName) {
        // 获取所有子部门的id
        List<Integer> deptIdList = getDeptIdListByParentId(deptId);
        if(deptIdList == null) {
            return new ArrayList<>();
        }
        if(deptIdList.isEmpty() || StringUtils.isEmpty(deptName)) {
            return deptIdList;
        }
        Set<Integer> deptIdSet = new HashSet<>(deptIdList);
        List<Integer> result = new ArrayList<>();
        // 获取和deptName相似的部门id
        deptIdList = deptDao.selectIdListByLikeDeptName(deptName);
        for(Integer id : deptIdList) {
            if(deptIdSet.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Dept> getAllDeptMap() {
        List<Dept> deptList = deptDao.selectAllAndAscOrderByLevel();
        Map<Integer, Dept> map = new HashMap<>();
        for(Dept dept : deptList) {
            map.put(dept.getId(), dept);
        }
        return map;
    }

    @Override
    public List<DeptBriefVO> getDeptBriefListFromDeptMapByChildId(Map<Integer, Dept> deptMap, Integer deptId) {
        if(deptId == null || deptId == 0 || CollectionUtils.isEmpty(deptMap)) {
            return new ArrayList<>();
        }
        List<DeptBriefVO> deptBriefVOList = new ArrayList<>();
        while (deptId != null && deptId != 0) {
            Dept dept = deptMap.get(deptId);
            if(dept == null) {
                break;
            }
            deptBriefVOList.add(CopyBeanUtil.copy(dept, DeptBriefVO.class));
            deptId = dept.getParentId();
        }
        return deptBriefVOList;
    }

    @Override
    public void saveDept(List<DeptDTO> deptDTOList) {
        if(CollectionUtils.isEmpty(deptDTOList)) {
            return;
        }

        List<Dept> deptList = new ArrayList<>();

        Map<DeptDTO, Integer> deptDTOMap = new HashMap<>();

        Queue<DeptDTO> queue = new LinkedList<>();
        DeptDTO deptDTO = new DeptDTO();
        deptDTO.setChildDeptDTOList(deptDTOList);
        queue.offer(deptDTO);

        int level = 0;
        while(!queue.isEmpty()) {
            int size = queue.size();
            while(size-- > 0) {
                DeptDTO dto = queue.poll();
                if(dto == null) {
                    continue;
                }
                Dept dept = CopyBeanUtil.copy(dto, Dept.class);
                if(level == 0) {
                    dept.setId(0);
                } else {
                    dept.setLevel(level);
                    dept.setId(Integer.parseInt(MathUtil.getRandomNumber(5) + "" + System.currentTimeMillis() % 1000));
                    dept.setParentId(deptDTOMap.get(dto));
                    deptList.add(dept);
                }

                if(CollectionUtils.isEmpty(dto.getChildDeptDTOList())) {
                    dept.setLeaf(true);
                    continue;
                }
                dept.setLeaf(false);

                for(DeptDTO temp : dto.getChildDeptDTOList()) {
                    deptDTOMap.put(temp, dept.getId());
                    queue.offer(temp);
                }
            }
            level++;
        }
        deptDao.insertBatch(deptList);
    }

    private void getChildDeptIdListByParentId(Set<Integer> deptIdSet, Integer deptId) throws LogiSecurityException {
        if(deptId == null) {
            return;
        }
        deptIdSet.add(deptId);
        List<Integer> childIdList = deptDao.selectIdListByParentId(deptId);
        for(Integer childId : childIdList) {
            if(deptIdSet.contains(childId)) {
                // 如果出现这种情况，则数据有误，中断递归
                // 这是为了防止，child的parentId是parent，但parent的parentId却是child
                throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
            }
            try {
                getChildDeptIdListByParentId(deptIdSet, childId);
            } catch (LogiSecurityException e) {
                throw new LogiSecurityException(ResultCode.DEPT_DATA_ERROR);
            }
        }
    }
}
