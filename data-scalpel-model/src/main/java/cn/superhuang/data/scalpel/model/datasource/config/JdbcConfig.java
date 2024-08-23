package cn.superhuang.data.scalpel.model.datasource.config;

import cn.superhuang.data.scalpel.model.enumeration.DbType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JdbcConfig extends DatasourceConfig {
    public static final String JDBC_OPTIONS_KEY_SCHEMA = "SYS_SCHEMA";
    public static final String JDBC_OPTIONS_KEY_ORACLE_CONNECTION_TYPE = "SYS_CONNECTION_TYPE";
    public static final String JDBC_OPTIONS_KEY_CK_CLUSTER_NAME = "SYS_CK_CLUSTER_NAME";
    public static Set<String> sysParam = new HashSet<>();

    static {
        sysParam.add(JDBC_OPTIONS_KEY_SCHEMA);
        sysParam.add(JDBC_OPTIONS_KEY_ORACLE_CONNECTION_TYPE);
        sysParam.add(JDBC_OPTIONS_KEY_CK_CLUSTER_NAME);
    }


    public static final String JDBC_DB_TYPE = "type";
    public static final String JDBC_DB = "database";
    public static final String JDBC_HOST = "host";
    public static final String JDBC_PORT = "port";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";

    public DbType getDbType() {
        return DbType.valueOf(getParams().get(JDBC_DB_TYPE));
    }

    public String getDatabase() {
        return getParams().get(JDBC_DB);
    }

    public String getHost() {
        return getParams().get(JDBC_HOST);
    }

    public Integer getPort() {
        return Integer.parseInt(getParams().get(JDBC_PORT));
    }

    public String getUsername() {
        return getParams().get(JDBC_USERNAME);
    }

    public String getPassword() {
        return getParams().get(JDBC_PASSWORD);
    }

    public String getSchema() {
        return getOptions().get(JDBC_OPTIONS_KEY_SCHEMA);
    }


    public void setDbType(DbType jdbcDbType) {
        getParams().put(JDBC_DB_TYPE, jdbcDbType.name());
    }

    public void setDatabase(String database) {
        getParams().put(JDBC_DB, database);
    }

    public void setHost(String host) {
        getParams().put(JDBC_HOST, host);
    }

    public void setPort(Integer port) {
        getParams().put(JDBC_PORT, String.valueOf(port));
    }

    public void setUsername(String username) {
        getParams().put(JDBC_USERNAME, username);
    }

    public void setPassword(String password) {
        getParams().put(JDBC_PASSWORD, password);
    }

    public void setSchema(String schema) {
        putOption(JDBC_OPTIONS_KEY_SCHEMA, schema);
    }


    public String getUniqueId() {
        String schema = getSchema();
        if (schema == null) {
            schema = "";
        }
        return getHost() + "_" + getPort() + "_" + getDatabase() + "_" + getSchema() + "_" + getUsername();
    }

    @JsonIgnore
    public Map<String, String> getParamsExcludeSys() {
        return getOptions().entrySet().stream().filter(entry -> !sysParam.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public String formatParams(Map<String, String> extraParams, CharSequence eqChar, CharSequence separator) {
        Map<String, String> newParmasMap = getParamsExcludeSys();
        extraParams.putAll(newParmasMap);
        return extraParams.entrySet().stream().map(e -> e.getKey() + eqChar + e.getValue()).collect(Collectors.joining(separator));
    }


}
