package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.enumeration.DbType;

import java.io.Serial;
import java.io.Serializable;

public class TDEngineDialect extends TDEngineRSDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = 5619039180608815287L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.TD_ENGINE;
    }


}
