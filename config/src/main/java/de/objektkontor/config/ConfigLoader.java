package de.objektkontor.config;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ConfigLoader {

    private final Configuration backend;
    private final String bundle;

    public ConfigLoader(Configuration backend, String bundle) {
        this.backend = backend;
        this.bundle = bundle;
    }

    public <C> C loadConfig(C config) {
        return loadConfig(null, config);
    }

    public <C> C loadConfig(String prefix, C config) {
        Collection<Field> fields = getConfigParameterFields(config.getClass());
        if (fields.size() == 0)
            throw new IllegalArgumentException("No configuration parameters found in class: " + config.getClass());
        loadConfigParameters(prefix, config, fields);
        return config;
    }

    public void loadConfigParameters(String prefix, Object config, Collection<Field> fields) {
        for (Field field : fields) {
            String name = field.getAnnotation(ConfigParameter.class).value();
            if (name.equals(ConfigParameter.FIELD_NAME))
                name = field.getName();
            String key = prefix == null ? "" : prefix;
            key += name.length() > 0 ? key.length() > 0 ? "." + name : name : "";
            field.setAccessible(true);
            try {
                Object value = field.get(config);
                Collection<Field> childFields = getConfigParameterFields(value == null ? field.getType() : value.getClass());
                if (childFields.size() > 0) {
                    if (value == null) {
                        try {
                            value = field.getType().newInstance();
                        } catch (InstantiationException e) {
                            throw new IllegalArgumentException("Error creating instance of configuration parameter: " + field);
                        }
                        field.set(config, value);
                    }
                    loadConfigParameters(key, value, childFields);
                    continue;
                }
                if (key.length() == 0)
                    throw new IllegalArgumentException("Empty key for configuration parameter: " + field);
                field.set(config, loadParameterValue(key, field, value));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object loadParameterValue(String key, Field field, Object defaultValue) {
        Class<?> type = field.getType();
        if (type == String.class)
            return backend.getStringValue(bundle, key, (String) defaultValue);
        if (type == int.class || type == Integer.class)
            return backend.getIntValue(bundle, key, (Integer) defaultValue);
        if (type == boolean.class || type == Boolean.class)
            return backend.getBooleanValue(bundle, key, (Boolean) defaultValue);
        if (type == Date.class)
            return backend.getDateValue(bundle, key, (Date) defaultValue);
        if (Enum.class.isAssignableFrom(type))
            return backend.getEnumValue(bundle, key, (Enum<?>) defaultValue);
        throw new IllegalArgumentException("Unsupported type for configuration parameter: " + field);
    }

    private Collection<Field> getConfigParameterFields(Class<?> configClass) {
        List<Field> result = new LinkedList<>();
        Class<?> currentClass = configClass;
        while (currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields())
                if (field.isAnnotationPresent(ConfigParameter.class))
                    result.add(field);
            currentClass = currentClass.getSuperclass();
        }
        return result;
    }
}
