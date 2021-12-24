package org.door.test.service;

import org.door.core.ano.Limiting;
import org.door.core.ano.LimitingGroup;
import org.springframework.stereotype.Service;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/24 9:41
 * @Description:
 **/
@Service
public class MethodGroup {

    @LimitingGroup(group = "Group",permitsPerTime = 2)
    public void groupInit1(){
    }

    @LimitingGroup(group = "Group2",permitsPerTime = 2)
    public void groupInit2(){
    }

    @Limiting(permits = 2,groupName = "Group")
    public void groupTest1(){

    }

    @Limiting(permits = 1,groupName = "Group")
    public void groupTest2(){

    }

    @Limiting(permits = 1,groupName = "Group2")
    public void groupTest3(){

    }




}
