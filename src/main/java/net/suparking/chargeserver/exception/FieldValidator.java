package net.suparking.chargeserver.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class FieldValidator {
    private static final Logger log = LoggerFactory.getLogger(FieldValidator.class);
    public boolean validate() {
        Field[] fields = this.getClass().getDeclaredFields();
        if (this.getClass().isAnnotationPresent(ParamNotNull.class)) {
            for (Field field: fields) {
                field.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = field.get(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (fieldValue == null) {
                    log.error(field.getName() + " cannot be null");
                    return false;
                }
            }
        } else {
            for (Field field: fields) {
                if (field.isAnnotationPresent(ParamNotNull.class)) {
                    field.setAccessible(true);
                    Object fieldValue = null;
                    try {
                        fieldValue = field.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    if (fieldValue == null) {
                        log.error(field.getName() + " cannot be null");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
