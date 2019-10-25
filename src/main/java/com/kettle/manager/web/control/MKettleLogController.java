package com.kettle.manager.web.control;

import com.kettle.manager.core.ResultModel;
import com.kettle.manager.web.entity.MRepository;
import com.kettle.manager.kettle.config.KettleRepositoryLoadConfigure;
import com.kettle.manager.web.entity.MKettleLog;
import com.kettle.manager.web.service.MKettleLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/kettle/log")
public class MKettleLogController {

    @Autowired
    private MKettleLogService mKettleLogService;

    @RequestMapping("/getLogList")
    public ResultModel getLogList(String repositoryId,@RequestParam(required = false) String name,int pageIndex,int pageSize){

        ResultModel rm = null;
        try {

            Sort sort = new Sort(Sort.Direction.DESC, "startTime");
            Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

            MRepository mRepository = KettleRepositoryLoadConfigure.repositoryMap.get(repositoryId);

            Specification<MKettleLog> specification = ((root, query, criteriaBuilder) -> {

                List<Predicate> list =  new ArrayList<>();

                list.add(criteriaBuilder.equal(root.get("jobRepositoryName"),mRepository.getName()));

                if (StringUtils.isNoneBlank(name)){
                    list.add(criteriaBuilder.like(root.get("jobName"),"%"+name+"%"));
                }

                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            });

            List<MKettleLog> mKettleLogs = mKettleLogService.listMKettleLogByParam(specification, pageable);
            int count = (int) mKettleLogService.countMKettleLog(specification);

            rm = new ResultModel(count,pageIndex,pageSize);
            rm.setState("200");
            rm.setData(mKettleLogs);
        }catch (Exception ex){
            rm = new ResultModel();
            rm.setState("500");
            rm.setMsgInfo("列表获取失败");
            log.error(ex.getMessage());
        }
        return rm;
    }


}
