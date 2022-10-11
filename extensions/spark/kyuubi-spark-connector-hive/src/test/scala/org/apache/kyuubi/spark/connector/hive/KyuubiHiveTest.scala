/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kyuubi.spark.connector.hive

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

import org.apache.kyuubi.KyuubiFunSuite
import org.apache.kyuubi.spark.connector.common.LocalSparkSession

abstract class KyuubiHiveTest extends KyuubiFunSuite with Logging {

  private var spark: SparkSession = _

  override def beforeEach(): Unit = {
    super.beforeAll()
    getOrCreateSpark()
  }

  override def afterEach(): Unit = {
    super.afterAll()
    LocalSparkSession.stop(spark)
  }

  def getOrCreateSpark(): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .set("spark.ui.enabled", "false")
      .set("spark.sql.catalogImplementation", "hive")
      .set("spark.sql.catalog.hive", classOf[HiveTableCatalog].getName)
      .set("javax.jdo.option.ConnectionURL", "jdbc:derby:memory:memorydb;create=true")
      .set("javax.jdo.option.ConnectionDriverName", "org.apache.derby.jdbc.EmbeddedDriver")

    spark = SparkSession.builder.config(sparkConf).getOrCreate()
  }

  def withSparkSession[T](conf: Map[String, String] = Map.empty[String, String])(
      f: SparkSession => T): T = {
    assert(spark != null)
    conf.foreach {
      case (k, v) => spark.sessionState.conf.setConfString(k, v)
    }
    f(spark)
  }
}
