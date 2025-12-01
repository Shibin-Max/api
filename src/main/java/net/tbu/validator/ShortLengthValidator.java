package net.tbu.validator;

import net.tbu.annotation.ShortLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author FT hao.yu
 * @since 2024-09-30
 * short类型验证器
 */
public class ShortLengthValidator implements ConstraintValidator<ShortLength, Short> {

    private int min;
    private int max;

    @Override
    public void initialize(ShortLength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Short value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 或者根据需求返回 false
        }
        int length = String.valueOf(Math.abs(value)).length(); // 计算绝对值的长度
        return length >= min && length <= max; // 检查长度是否在范围内
    }
}
