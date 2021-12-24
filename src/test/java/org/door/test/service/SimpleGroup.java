package org.door.test.service;

import lombok.extern.slf4j.Slf4j;
import org.door.core.ano.Limiting;
import org.door.core.ano.LimitingGroup;
import org.springframework.stereotype.Service;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/23 17:37
 * @Description:
 **/
@Slf4j
@Service
public class SimpleGroup {

    @Limiting(permits = 1, group = @LimitingGroup(permitsPerTime = 1))
    public void simpleGroup1() {
        log.info("simpleGroup is run");
    }

    @Limiting(permits = 3, group = {
            @LimitingGroup(permitsPerTime = 10),
            @LimitingGroup(permitsPerTime = 5)
    })
    public void simpleGroup2() {
        log.info("simpleGroup is run");
    }

    @Limiting(permits = 3, group = {
            @LimitingGroup(group = "group3",permitsPerTime = 6),
            @LimitingGroup(permitsPerTime = 5)
    })
    public void simpleGroup3() {
        log.info("simpleGroup is run");
    }

    @Limiting(permits = 3, groupName = "group3",group = {
            @LimitingGroup(group = "group4",permitsPerTime = 10),
            @LimitingGroup(permitsPerTime = 9)
    })
    public void simpleGroup4() {
        log.info("simpleGroup is run");
    }


}
