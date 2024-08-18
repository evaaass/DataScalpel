package cn.superhuang.data.scalpel.admin.task.executor;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DockerExecutor implements TaskExecutor {

    @Override
    public String submitTask() {

        return null;
    }

    @Override
    public void getTaskStatus(String taskId) {

    }

    public static void main(String[] args) throws IOException {
        //docker run -itd -name xxxx -v xx:xx imageName  参数
        String command = "ping 10.0.0.1";
        //接收正常结果流
        ByteArrayOutputStream susStream = new ByteArrayOutputStream();
        //接收异常结果流
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        int code = exec.execute(commandLine);
        System.out.println("result code: " + code);
        // 不同操作系统注意编码，否则结果乱码
        String suc = susStream.toString("GBK");
        String err = errStream.toString("GBK");
        System.out.println(suc);
        System.out.println(err);
    }
}
