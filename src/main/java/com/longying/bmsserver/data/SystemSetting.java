package com.longying.bmsserver.data;

import lombok.Builder;
import lombok.Data;

/**
 * Create by chenglong on 2021/2/28
 */
@Data
@Builder
public class SystemSetting {

    private String name;

    private String value;
}
