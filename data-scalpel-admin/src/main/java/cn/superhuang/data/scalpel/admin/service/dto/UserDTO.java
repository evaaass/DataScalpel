package cn.superhuang.data.scalpel.admin.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8454064365046566081L;

    private String id;

    private String login;


}
