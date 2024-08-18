package cn.superhuang.data.scalpel.admin.app.datasource.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * A DatasourceDefinition.
 */
@Entity
@Table(name = "admin_datasource_definition")
@Data
public class DatasourceDefinition implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @Column(name = "id")
    private String id;

    @Column(name = "jhi_group")
    private String group;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

}
