package org.door.test.service;

import lombok.extern.slf4j.Slf4j;
import org.door.core.ano.Limiting;
import org.door.core.ano.LimitingGroup;
import org.springframework.stereotype.Service;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/24 9:57
 * @Description:
 **/
@Service
@LimitingGroup(permitsPerTime = 2,time = 10)
public class ClassGroup {

    @Limiting(permits = 2)
    public void test1(){

    }

    @Limiting(permits = 1)
    public void test2(){

    }

}
