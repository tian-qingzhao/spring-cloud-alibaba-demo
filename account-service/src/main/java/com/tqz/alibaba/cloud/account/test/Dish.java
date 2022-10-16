package com.tqz.alibaba.cloud.account.test;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author tianqingzhao
 * @since 2021/2/26 9:56
 */
@Data
@AllArgsConstructor
public class Dish {
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;


    public enum Type {
        MEAT,
        FISH,
        OTHER
    }
}



