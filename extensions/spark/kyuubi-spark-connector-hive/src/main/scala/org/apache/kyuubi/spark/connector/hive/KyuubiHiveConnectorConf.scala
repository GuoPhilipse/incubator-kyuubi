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

import java.util.Locale

object KyuubiHiveConnectorConf {

  import org.apache.spark.sql.internal.SQLConf.buildStaticConf

  val EXTERNAL_CATALOG_SHARE_POLICY =
    buildStaticConf("spark.sql.kyuubi.hive.connector.externalCatalog.share.policy")
      .internal()
      .doc(s"Indicates the share policy for the externalCatalog in the Kyuubi Connector, we use" +
        "'all' by default. " +
        "<li>ONE_FOR_ONE: Indicate to an external catalog is used by only one HiveCatalog. </li> " +
        "<li>ONE_FOR_ALL: Indicate to an external catalog is shared globally with the " +
        "HiveCatalogs with the same catalogName. </li> ")
      .version("1.7.0")
      .stringConf
      .transform(policy => policy.toUpperCase(Locale.ROOT))
      .checkValue(
        policy => Set("ONE_FOR_ONE", "ONE_FOR_ALL").contains(policy),
        "Invalid value for 'spark.sql.kyuubi.hive.connector.externalCatalog.share.policy'." +
          "Valid values are 'ONE_FOR_ONE', 'ONE_FOR_ALL'.")
      .createWithDefault(OneForAllPolicy.name)
}
