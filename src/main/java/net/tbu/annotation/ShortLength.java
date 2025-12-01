package net.tbu.annotation;
import net.tbu.validator.ShortLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FT hao.yu
 * @since 2024-09-30
 * short类型验证注解
 */
@Constraint(validatedBy = ShortLengthValidator.class) // 指定验证器
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ShortLength {

    String message() default "Invalid length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 1; // 最小位数

    int max() default 5; // 最大位数（对于 short，通常不超过 5 位）
}