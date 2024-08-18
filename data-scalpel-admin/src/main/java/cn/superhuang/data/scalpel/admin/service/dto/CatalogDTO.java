package cn.superhuang.data.scalpel.admin.service.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class CatalogDTO implements Serializable {

    private String id;

    private String type;

    private String name;

    private Integer index;

    private CatalogDTO parent;
}
