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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToolsTest {

    @DisplayName("正确转换普通日期、闰日、跨年日期及年份边界")
    @ParameterizedTest
    @CsvSource({
            "20261104, 2026-11-04",
            "20260102, 2026-01-02",
            "20240229, 2024-02-29",
            "20000229, 2000-02-29",
            "20191230, 2019-12-30",
            "20210101, 2021-01-01",
            "00010101, 0001-01-01",
            "99991231, 9999-12-31"
    })
    void formatsValidDates(String input, String expected) {
        assertEquals(expected, Tools.formatDate(input));
    }

    @DisplayName("拒绝空值、非八位数字及非法年份")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "2026114", "202611044", "2026-11-04", "2026ab04",
            " 20261104", "20261104 ", "        ", "２０２６１１０４",
            "+0261104", "00000101"
    })
    void rejectsInvalidFormats(String input) {
        assertThrows(IllegalArgumentException.class, () -> Tools.formatDate(input));
    }

    @DisplayName("拒绝不存在的月份、天数及非法闰日")
    @ParameterizedTest
    @ValueSource(strings = {"20260229", "19000229", "20261301", "20260001", "20261100", "20261131", "20260132"})
    void rejectsImpossibleDates(String input) {
        assertThrows(IllegalArgumentException.class, () -> Tools.formatDate(input));
    }

    @DisplayName("日期解析失败时保留原始异常并提供中文提示")
    @Test
    void preservesParseFailureCause() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Tools.formatDate("20260229"));

        assertEquals("日期不存在，请检查年月日", exception.getMessage());
        assertInstanceOf(DateTimeParseException.class, exception.getCause());
    }
}
