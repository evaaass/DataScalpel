package cn.superhuang.data.scalpel.spark.core.util

import scala.collection.JavaConverters.mapAsScalaMapConverter

object ScalaUtil {
  def convertToScalaImmutableMap[K, V](javaMap: java.util.Map[K, V]): Map[K, V] = {
    javaMap.asScala.toMap
  }

}
