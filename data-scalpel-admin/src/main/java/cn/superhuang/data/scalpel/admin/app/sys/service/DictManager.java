package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import cn.superhuang.data.scalpel.admin.app.sys.repository.DictRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictManager implements InitializingBean {

    public static final String DICT_TYPE_SYSTEM_SETTING = "SysSetting";
    @Resource
    private DictRepository dictRepository;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化字典");
        Boolean init = false;
        for (Dict dict : dictRepository.findAllByType(DICT_TYPE_SYSTEM_SETTING)) {
            if (dict.getName().equals("init")) {
                init = Boolean.parseBoolean(dict.getValue());
            }
        }
        if(!init) {
            dictRepository.deleteAll();
            List<Dict> dicts = objectMapper.readValue(ResourceUtil.getUtf8Reader("init/Dict.json"),new TypeReference<List<Dict>>(){});
            dictRepository.saveAll(dicts);
        }
    }
}
