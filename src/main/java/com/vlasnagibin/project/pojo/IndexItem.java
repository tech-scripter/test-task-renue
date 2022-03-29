package com.vlasnagibin.project.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Для создания массива объектов. Похож на словарь (ключ-значение)
 */
@AllArgsConstructor
@Getter
public final class IndexItem {
    private final String columnValue;
    private final long offset;

}
