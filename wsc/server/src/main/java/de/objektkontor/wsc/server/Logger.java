package de.objektkontor.wsc.server;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;


public class Logger extends org.apache.felix.framework.Logger {

    private final org.slf4j.Logger log;

    public Logger(org.slf4j.Logger log) {
        this.log = log;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void doLog(Bundle bundle, ServiceReference sr, int level, String msg, Throwable throwable) {
        switch (level) {
        case org.apache.felix.framework.Logger.LOG_DEBUG:
            log.debug(msg, throwable);
            break;
        case org.apache.felix.framework.Logger.LOG_INFO:
            log.info(msg, throwable);
            break;
        case org.apache.felix.framework.Logger.LOG_WARNING:
            log.warn(msg, throwable);
            break;
        case org.apache.felix.framework.Logger.LOG_ERROR:
            log.error(msg, throwable);
            break;
        default:
            throw new IllegalStateException("unsupported level");
        }
    }
}
