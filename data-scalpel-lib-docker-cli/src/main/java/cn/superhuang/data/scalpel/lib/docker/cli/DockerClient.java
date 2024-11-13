package cn.superhuang.data.scalpel.lib.docker.cli;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.superhuang.data.scalpel.lib.docker.cli.model.DockerContainerStat;
import cn.superhuang.data.scalpel.lib.docker.cli.model.DockerContainerStatus;

import java.util.List;

public class DockerClient {

    public void killContainer(String containerId) {
        String[] commands = new String[]{"docker", "kill", containerId};
        System.out.println(executeCommand(commands));
    }

    public DockerContainerStat getContainerStat(String containerId) {
        String[] commands = {"docker", "container", "stats", "--format", "json", "--no-stream", containerId};
        String statJson = executeCommand(commands);
        if (StrUtil.isBlank(statJson)) {
            statJson = "{}";
        }
        return JSONUtil.toBean(statJson, DockerContainerStat.class);
    }

    public DockerContainerStatus getContainerStatus(String containerId) {
        String[] commands = {"docker", "ps", "-a", "--format", "json", "--filter", StrUtil.format("id={}", containerId)};
        String containerState = executeCommand(commands);
        if (StrUtil.isBlank(containerState)) {
            containerState = "{}";
        }
        return JSONUtil.toBean(containerState, DockerContainerStatus.class);
    }

    public String getContainerLog(String containerId) {
        String[] commands = {"docker", "logs", containerId};
        return executeCommand(commands);
    }

    public void startContainer(String containerId) {
        String[] commands = {"docker", "start", containerId};
        String res = executeCommand(commands);
        System.out.println(res);
    }

    public String executeCommand(String... commands) {
        return RuntimeUtil.execForStr(commands).trim();
    }

    public String executeCommand(List<String> commands) {
        return executeCommand(commands.toArray(new String[]{}));
    }

    public static void main(String[] args) {
        DockerClient client = new DockerClient();
        System.out.println(client.getContainerStatus("55005f1f641c"));
        System.out.println(client.getContainerStat("55005f1f641c"));
        client.killContainer("55005f1f641c");
    }
}
