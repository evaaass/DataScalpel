package cn.superhuang.data.scalpel.admin.app.dispatcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DockerContainerStatus {
    @JsonProperty(value = "ID")
    private String id;
    @JsonProperty(value = "Names")
    private String names;
    @JsonProperty(value = "Image")
    private String image;
    @JsonProperty(value = "Networks")
    private String networks;
    @JsonProperty(value = "Ports")
    private String ports;
    @JsonProperty(value = "RunningFor")
    private String runningFor;

    //exited,running
    @JsonProperty(value = "State")
    private String state;
    @JsonProperty(value = "Status")
    private String status;
}