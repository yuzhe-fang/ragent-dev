/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nageoffer.ai.ragent.framework.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 通用工具类。
 */
public final class Tools {

    private Tools() {
    }

    /**
     * 将八位日期字符串转换为带连字符的日期，例如 20261104 转为 2026-11-04。
     *
     * @param date 八位 ASCII 数字组成的真实日期，年份范围为 0001 至 9999
     * @return yyyy-MM-dd 格式的日期字符串
     * @throws IllegalArgumentException 输入为空、格式错误或日期不存在时抛出
     */
    public static String formatDate(String date) {
        if (date == null || !date.matches("[0-9]{8}") || date.startsWith("0000")) {
            throw new IllegalArgumentException("日期必须为八位数字，且年份范围为 0001 至 9999");
        }
        try {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
            return parsedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("日期不存在，请检查年月日", exception);
        }
    }
}
