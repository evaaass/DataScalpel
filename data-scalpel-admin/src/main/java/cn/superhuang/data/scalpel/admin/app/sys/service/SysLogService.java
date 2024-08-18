package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.superhuang.data.scalpel.admin.app.sys.domain.SysLog;
import cn.superhuang.data.scalpel.admin.app.sys.repository.SysLogRepository;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class SysLogService {
    @Resource
    private SysLogRepository repository;


    //TODO 后面改成先进先出缓存队列,定时批量入库
    public void add(SysLog log) {
        log.setCreateTime(LocalDateTime.now());
        repository.save(log);
    }
}
