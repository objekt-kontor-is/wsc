package de.objektkontor.wsc.netty;

import io.netty.util.internal.JavassistTypeParameterMatcherGenerator;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import javassist.ClassPath;
import javassist.LoaderClassPath;

public class JavassistQuirks {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(JavassistQuirks.class);

    private ClassPathAppender classPathAppender = null;

    public void init(Object activator) {
        boolean noJavassist = SystemPropertyUtil.getBoolean("io.netty.noJavassist", false);
        if (noJavassist)
            log.info("Javassist: disabled (System property io.netty.noJavassist = true)");
        else
            try {
                classPathAppender = new ClassPathAppender(activator);
                log.info("Javassist: available");
            } catch (Throwable e) {
                log.info("Javassist: unavailable");
            }
    }

    public void close() {
        if (classPathAppender != null)
            classPathAppender.close();
    }

    private static class ClassPathAppender {

        private final ClassPath classPath;

        public ClassPathAppender(Object activator) throws Exception {
            classPath = new LoaderClassPath(activator.getClass().getClassLoader());
            JavassistTypeParameterMatcherGenerator.appendClassPath(classPath);
        }

        public void close() {
            classPath.close();
        }
    }
}
