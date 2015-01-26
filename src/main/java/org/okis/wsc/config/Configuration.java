package org.okis.wsc.config;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    public enum GeneralizeKeys { FROM_BEGIN, FROM_END };

    private final static DateFormat [] dateParsers = { new SimpleDateFormat("dd.MM.yyyy") };

    private final Map<String, Bundle> bundles = new ConcurrentHashMap<String, Bundle>();
    private final GeneralizeKeys generalizeKeys;

    public Configuration() {
        this(null);
    }

    public Configuration(GeneralizeKeys generalizeKeys) {
        this.generalizeKeys = generalizeKeys;
    }

    public String getStringValue(String bundle, String key) {
        return getStringValue(bundle, key, null);
    }

    public String getStringValue(String bundleName, String key, String defaultValue) {
        Bundle bundle = bundles.get(bundleName);
        if (bundle == null)
            synchronized (bundles) {
                bundle = bundles.get(bundleName);
                if (bundle == null) {
                    bundle = new Bundle(bundleName);
                    bundles.put(bundleName, bundle);
                }
            }
        if (generalizeKeys == null) {
            String value = bundle.getProperty(key);
            return value == null ? defaultValue : value;
        }
        String [] parts = key.split("\\.");
        switch (generalizeKeys) {
        case FROM_BEGIN:
            for (int i = 0; i < parts.length; i ++) {
                StringBuilder subkey = new StringBuilder(parts[i]);
                for (int j = i + 1; j < parts.length; j ++)
                    subkey.append(".").append(parts[j]);
                String value = bundle.getProperty(subkey.toString());
                if (value != null)
                    return value;
            }
            break;
        case FROM_END:
            for (int i = parts.length; i > 0 ; i --) {
                StringBuilder subkey = new StringBuilder(parts[0]);
                for (int j = 1; j < i; j ++)
                    subkey.append(".").append(parts[j]);
                String value = bundle.getProperty(subkey.toString());
                if (value != null)
                    return value;
            }
            break;
        default:
            throw new UnsupportedOperationException();
        }
        return defaultValue;
    }

    public int getIntValue(String bundle, String key) throws ValueFormatException {
        return getIntValue(bundle, key, 0);
    }

    public int getIntValue(String bundle, String key, int defaultValue) throws ValueFormatException {
        String value = getStringValue(bundle, key);
        if (value == null)
            return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    public long getLongValue(String bundle, String key) throws ValueFormatException {
        return getLongValue(bundle, key, 0);
    }

    public long getLongValue(String bundle, String key, long defaultValue) throws ValueFormatException {
        String value = getStringValue(bundle, key);
        if (value == null)
            return defaultValue;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    public boolean getBooleanValue(String bundle, String key) throws ValueFormatException {
        return getBooleanValue(bundle, key, false);
    }

    public boolean getBooleanValue(String bundle, String key, boolean defaultValue) throws ValueFormatException {
        String value = getStringValue(bundle, key);
        if (value == null)
            return defaultValue;
        if ("true".equalsIgnoreCase(value))
            return true;
        if ("false".equalsIgnoreCase(value))
            return false;
        throw new ValueFormatException(bundle, key, "supported values 'true' or 'false'");
    }

    public <T extends Enum<?>> T getEnumValue(String bundle, String key) throws ValueFormatException {
        return getEnumValue(bundle, key, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T getEnumValue(String bundle, String key, T defaultValue) throws ValueFormatException {
        String value = getStringValue(bundle, key);
        if (value == null)
            return defaultValue;
        try {
            return (T) Enum.valueOf(defaultValue.getClass(), value);
        } catch (IllegalArgumentException e) {
            throw new ValueFormatException(bundle, key, e);
        }
    }

    public Date getDateValue(String bundle, String key) throws ValueFormatException {
        return getDateValue(bundle, key, null);
    }

    public Date getDateValue(String bundle, String key, Date defaultDate) throws ValueFormatException {
        String value = getStringValue(bundle, key, null);
        if (value == null)
            return defaultDate;
        for (DateFormat parser : dateParsers)
            synchronized (parser) {
                try {
                    return parser.parse(value);
                } catch (ParseException e) {
                }
            }
        throw new ValueFormatException(bundle, key, "Invalid date or date format not supported");
    }

    public static class ValueFormatException extends RuntimeException {

        private static final long serialVersionUID = 253881058471256863L;

        private final String bundle;
        private final String key;

        public ValueFormatException(String bundle, String key, String message) {
            super(message);
            this.bundle = bundle;
            this.key = key;
        }

        public ValueFormatException(String bundle, String key, Throwable cause) {
            super(cause);
            this.bundle = bundle;
            this.key = key;
        }

        @Override
        public String getMessage() {
            return format("Invalid value for key '%s' in bundle '%s': " + super.getMessage(), bundle, key);
        }
    }

    public static class Bundle {

        private final Properties properties;

        public String getProperty(String key) {
            return properties.getProperty(key);
        }

        public boolean containsKey(String key) {
            return properties.containsKey(key);
        }

        public Bundle(String name) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null)
                loader = Configuration.class.getClassLoader();
            InputStream defaultBundle = loader.getResourceAsStream("default/" + name + ".properties");
            InputStream stageBundle = loader.getResourceAsStream(name + ".properties");
            if (defaultBundle == null && stageBundle == null)
                log.warn("No property files found in classpath for bundle: " + name);
            properties = new Properties();
            if (defaultBundle != null) {
                log.debug("defaultBundle " + name + " found");
                load(defaultBundle, name);
            }
            if (stageBundle != null) {
                log.debug("stageBundle " + name + " found");
                load(stageBundle, name);
            }
            if (log.isDebugEnabled() && !properties.isEmpty()) {
                log.debug("Loaded configuration values for bundle: " + name);
                log.debug(properties.toString());
            }
            cleanup();
        }

        private void load(InputStream in, String name) {
            try {
                properties.load(in);
            } catch (IOException e) {
                log.error("Error loading default configuration bundle: " + name, e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        private void cleanup() {
            Iterator<Object> i = properties.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String value = properties.getProperty(key);
                if (value != null && (value.isEmpty() || value.matches("\\s+")))
                    i.remove();
            }
        }
    }
}
