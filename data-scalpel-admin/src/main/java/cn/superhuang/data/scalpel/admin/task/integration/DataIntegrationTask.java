package cn.superhuang.data.scalpel.admin.task.integration;

import cn.superhuang.data.scalpel.admin.task.BaseTask;
import cn.superhuang.data.scalpel.admin.task.integration.source.DataIntegrationSource;
import cn.superhuang.data.scalpel.admin.task.integration.sink.FileSink;
import lombok.Data;

import java.util.List;
@Data
public class DataIntegrationTask extends BaseTask {
    private List<DataIntegrationSource> sources;
    private FileSink sink;
}