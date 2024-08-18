package cn.superhuang.data.scalpel.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.model.dto.ScriptDTO;
import cn.superhuang.data.scalpel.admin.repository.ScriptRepository;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskScript;
import cn.superhuang.data.scalpel.admin.model.dto.ScriptUpdateDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ScriptService {

    @Resource
    private ScriptRepository scriptRepository;

    public TaskScript save(ScriptDTO scriptDTO) {
        TaskScript script = BeanUtil.copyProperties(scriptDTO, TaskScript.class);
        return scriptRepository.save(script);
    }

    public void update(ScriptUpdateDTO scriptUpdateDTO) {
        scriptRepository.findById(scriptUpdateDTO.getId()).ifPresent(existingScript -> {
            BeanUtil.copyProperties(scriptUpdateDTO, existingScript, CopyOptions.create().ignoreNullValue());
            scriptRepository.save(existingScript);
        });
    }

    public void delete(String id) {
        scriptRepository.deleteById(id);
    }
}
