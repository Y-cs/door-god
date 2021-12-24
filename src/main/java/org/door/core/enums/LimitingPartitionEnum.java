package org.door.core.enums;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/20 14:03
 * @Description:
 **/
public enum LimitingPartitionEnum {

    /**
     * 组内公用
     */
    ALL,
    /**
     * 组内-每个IP独立使用
     */
    IP,
    /**
     * 组内根据编码内容独立使用
     */
    CODE,

}
