/**
 * Copyright 2014 Leidos
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leidos.ode.collector.datasource.pull.sdc;


import org.springframework.beans.factory.annotation.Value;

public class WarehouseConfig {

    public static final String JSESSIONID_KEY = "JSESSIONID";

    @Value("${sdc.warehouseURL}")
    public String warehouseURL;
    @Value("${sdc.httpWarehouseURL}")
    public String httpWarehouseURL;
    @Value("${sdc.keystoreFile}")
    public String keystoreFile;
    @Value("${sdc.keystorePassword}")
    public String keystorePassword;
    @Value("${sdc.casURL}")
    public String casURL;
    @Value("${sdc.casUserName}")
    public String casUserName;
    @Value("${sdc.casPassword}")
    public String casPassword;
    @Value("${sdc.jSessionID}")
    public String jSessionID;
    @Value("${sdc.requestDir}")
    public String requestDir;
    @Value("${sdc.logMessages}")
    public boolean logMessages;
    @Value("${sdc.writeToDisk}")
    public boolean writeToDisk;
    @Value("${sdc.binaryFiles}")
    public boolean binaryFiles;
    @Value("${sdc.responseDir}")
    public String responseDir;
    @Value("${sdc.systemDepositName}")
    public String systemDepositName;
    @Value("${sdc.encodeType}")
    public String encodeType;
    @Value("${sdc.depositFileDir}")
    public String depositFileDir;
    @Value("${sdc.depositDelay}")
    public int depositDelay;

    @Override
    public String toString() {
        return "WarehouseConfig [warehouseURL=" + warehouseURL
                + ", httpWarehouseURL=" + httpWarehouseURL + ", keystoreFile="
                + keystoreFile + ", keystorePassword=" + keystorePassword
                + ", casURL=" + casURL + ", casUserName=" + casUserName
                + ", casPassword=" + casPassword + ", jSessionID=" + jSessionID
                + ", requestDir=" + requestDir + ", logMessages=" + logMessages
                + ", writeToDisk=" + writeToDisk + ", binaryFiles="
                + binaryFiles + ", responseDir=" + responseDir
                + ", systemDepositName=" + systemDepositName + ", encodeType="
                + encodeType + ", depositFileDir=" + depositFileDir
                + ", depositDelay=" + depositDelay + "]";
    }

    public void postLoadCalculateValues() {
        httpWarehouseURL = "https" + warehouseURL.substring(warehouseURL.indexOf(":"));
    }

}
