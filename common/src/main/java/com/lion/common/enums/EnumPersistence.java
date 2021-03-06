package com.lion.common.enums;

import com.lion.core.AbstractEnumPersistence;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/29 下午4:46
 **/
@Component(value = "commonEnumPersistence")
@ConditionalOnWebApplication
@ConditionalOnClass({Reflections.class, AttributeConverter.class})
@Order(Ordered.LOWEST_PRECEDENCE)
public class EnumPersistence extends AbstractEnumPersistence {

    public EnumPersistence() {
        super(EnumPersistence.class.getPackage().getName());

    }

    public EnumPersistence(String packageName) {
        super(packageName);
    }
}
