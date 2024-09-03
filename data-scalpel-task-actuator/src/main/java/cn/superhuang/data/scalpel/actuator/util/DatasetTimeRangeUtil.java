package cn.superhuang.data.scalpel.actuator.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.app.task.model.TimeRangeStrategy;
import cn.superhuang.data.scalpel.app.task.model.TimeRangeStrategyType;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Date;

@Slf4j
public class DatasetTimeRangeUtil {

    public static final String DATE_FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";

    public static Dataset<Row> filterByTimeRangeStrategy(Dataset<Row> dataset, String timeFieldName, Long planTriggerTimestamp, TaskCycleType cycleType, TimeRangeStrategy timeStrategy) {
        if (timeStrategy.getType() == TimeRangeStrategyType.ALL || StrUtil.isBlank(timeFieldName)) {
            return dataset;
        }
        StructType structType = dataset.schema();
        Boolean containsTimeField = ArrayUtil.contains(structType.fieldNames(), timeFieldName);
        if (!containsTimeField) {
            throw new RuntimeException("数据不包含时间字段：" + containsTimeField);
        }
        StructField structField = structType.apply(timeFieldName);

        Date startTime = null;
        Date endTime = null;
        Date planTriggerTime = new Date(planTriggerTimestamp);
        switch (timeStrategy.getType()) {
            case T1:
                log.debug("-------------参考基准时间：" + planTriggerTimestamp);
                if (cycleType == null) {
                    throw new RuntimeException("周期采集时，周期类型不能为空");
                } else if (cycleType == TaskCycleType.HOUR) {
                    endTime = DateUtil.beginOfHour(planTriggerTime);
                    startTime = DateUtil.offsetHour(endTime, -1);
                } else if (cycleType == TaskCycleType.DAY) {
                    endTime = DateUtil.beginOfDay(planTriggerTime);
                    startTime = DateUtil.offsetDay(endTime, -1);
                } else if (cycleType == TaskCycleType.WEEK) {
                    endTime = DateUtil.beginOfWeek(planTriggerTime);
                    startTime = DateUtil.offsetWeek(endTime, -1);
                } else if (cycleType == TaskCycleType.MONTH) {
                    endTime = DateUtil.beginOfMonth(planTriggerTime);
                    startTime = DateUtil.offsetMonth(endTime, -1);
                } else if (cycleType == TaskCycleType.YEAR) {
                    endTime = DateUtil.beginOfYear(planTriggerTime);
                    startTime = DateUtil.offsetMonth(endTime, -12);
                }
                break;
            case RANGE:
                startTime = new Date(timeStrategy.getStartTime());
                endTime = new Date(timeStrategy.getEndTime());
                break;
            default:
                break;
        }

        DataType dataType = structField.dataType();
        if (DataTypes.TimestampType.equals(dataType)) {
            String startTimeStr = DateUtil.format(startTime, DATE_FORMAT_TIMESTAMP);
            String endTimeStr = DateUtil.format(endTime, DATE_FORMAT_TIMESTAMP);
            dataset = dataset.where(dataset.col(SparkUtil.quoteIdentifier(timeFieldName)).$greater$eq(startTimeStr).and(dataset.col(SparkUtil.quoteIdentifier(timeFieldName)).$less(endTimeStr)));
        } else if (DataTypes.DateType.equals(dataType)) {
            String startTimeStr = DateUtil.format(startTime, DATE_FORMAT_DATE);
            String endTimeStr = DateUtil.format(endTime, DATE_FORMAT_DATE);
            dataset = dataset.where(dataset.col(SparkUtil.quoteIdentifier(timeFieldName)).$greater$eq(startTimeStr).and(dataset.col(SparkUtil.quoteIdentifier(timeFieldName)).$less(endTimeStr)));
        } else if (DataTypes.LongType.equals(dataType)) {
            dataset = dataset.where(dataset.col(SparkUtil.quoteIdentifier(timeFieldName)).$greater$eq(startTime.getTime()).and(dataset.col(SparkUtil.quoteIdentifier(timeFieldName)).$less(endTime.getTime())));
        } else {
            throw new RuntimeException(StrUtil.format("不支持时间过滤，字段{}类型为{}", timeFieldName, dataType.toString()));
        }
        return dataset;
    }
}
