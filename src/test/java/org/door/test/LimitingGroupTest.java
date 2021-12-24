package org.door.test;

import lombok.extern.slf4j.Slf4j;
import org.door.core.exception.LimitingException;
import org.door.test.service.ClassGroup;
import org.door.test.service.MethodGroup;
import org.door.test.service.SimpleGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @Author: YuanChangShuai
 * @Date: 2021/12/23 17:43
 * @Description:
 **/
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LimitingGroupTest {

    private final SimpleGroup simpleGroup;

    private final MethodGroup methodGroup;

    private final ClassGroup classGroup;

    @Autowired
    public LimitingGroupTest(SimpleGroup simpleGroup, MethodGroup methodGroup, ClassGroup classGroup) {
        this.simpleGroup = simpleGroup;
        this.methodGroup = methodGroup;
        this.classGroup = classGroup;
    }

    @Test
    public void testSimpleGroup1() {
        try {
            simpleGroup.simpleGroup1();
            simpleGroup.simpleGroup1();
        } catch (Exception e) {
            checkException(e);
        }
    }

    @Test
    public void testSimpleGroup2() {
        try {
            simpleGroup.simpleGroup2();
            simpleGroup.simpleGroup2();
        } catch (Exception e) {
            checkException(e);
        }
    }

    @Test
    public void testSimpleGroup3() {
        try {
            simpleGroup.simpleGroup3();
            simpleGroup.simpleGroup3();
        } catch (Exception e) {
            checkException(e);
        }
    }

    @Test
    public void testSimpleGroup4() {
        try {
            simpleGroup.simpleGroup4();
            simpleGroup.simpleGroup4();
            simpleGroup.simpleGroup4();
        } catch (Exception e) {
            checkException(e);
        }
    }

    @Test
    public void testMethodGroup1() {
        try {
            methodGroup.groupTest1();
            methodGroup.groupTest2();
        } catch (Exception e) {
            checkException(e);
        }
    }
    @Test
    public void testMethodGroup3() {
        try {
            methodGroup.groupTest1();
            methodGroup.groupTest3();
        } catch (Exception e) {
        }
    }

    @Test
    public void testClassGroup(){
        try {
            classGroup.test1();
            classGroup.test2();
        } catch (Exception e) {
            checkException(e);
        }
    }

    public void checkException(Exception exception) {
        Throwable undeclaredThrowable = ((UndeclaredThrowableException) exception).getUndeclaredThrowable();
        log.info(undeclaredThrowable.getMessage());
        assert undeclaredThrowable instanceof LimitingException;
    }

}
