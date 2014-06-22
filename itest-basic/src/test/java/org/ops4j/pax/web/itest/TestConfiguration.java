package org.ops4j.pax.web.itest;

import static org.ops4j.pax.exam.CoreOptions.bootDelegationPackages;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.linkBundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.ops4j.lang.Ops4jException;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.util.PathUtils;


public class TestConfiguration {

    public static Option undertowBundles() {
        return composite(
//            linkBundle("io.undertow.core"),
//            linkBundle("io.undertow.servlet"),
//            linkBundle("org.jboss.xnio.api"),
//            linkBundle("org.jboss.xnio.nio"),
            mavenBundle("org.ops4j.pax.tipi", "org.ops4j.pax.tipi.undertow.servlet", "1.0.15.1"),
            mavenBundle("org.ops4j.pax.tipi", "org.ops4j.pax.tipi.undertow.core", "1.0.15.1"),
            mavenBundle("org.ops4j.pax.tipi", "org.ops4j.pax.tipi.xnio.api", "3.2.2.1"),
            mavenBundle("org.ops4j.pax.tipi", "org.ops4j.pax.tipi.xnio.nio", "3.2.2.1").noStart(),
            linkBundle("org.jboss.logging.jboss-logging"),
            mavenBundle("javax.annotation", "javax.annotation-api", "1.2"),
            linkBundle("javax.servlet-api")
            );
    }
    
    public static Option logbackBundles() {
        return composite(
            systemProperty("logback.configurationFile").value(
                "file:" + PathUtils.getBaseDir() + "/src/test/resources/logback.xml"),

                
            linkBundle("slf4j.api"), 
            linkBundle("ch.qos.logback.core"),
            linkBundle("ch.qos.logback.classic"));
    }
    

    
    public static Option paxCdiSharedBundles() {
        return composite(
            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-extender"),
            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-extension"),
            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-api"),
            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-spi"),
            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-servlet"));
    }
    
    public static Option paxCdiWithWeldBundles() {

        Properties props = new Properties();
        try {
            props.load(TestConfiguration.class.getResourceAsStream("/systemPackages.properties"));
        }
        catch (IOException exc) {
            throw new Ops4jException(exc);
        }
        

        return composite(
            // do not treat javax.annotation as system package
            frameworkProperty("org.osgi.framework.system.packages").value(props.get("org.osgi.framework.system.packages")),

            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-weld"),
            workspaceBundle("org.ops4j.pax.cdi", "pax-cdi-undertow-weld"),
            mavenBundle("com.google.guava", "guava", "13.0.1"),
            mavenBundle("org.jboss.weld", "weld-osgi-bundle", "2.1.2.Final"));
    }
    
    public static Option mojarraBundles() {
        return composite(
            bootDelegationPackages(
                "org.xml.sax", "org.xml.*", "org.w3c.*", "javax.xml.*",
                "javax.activation.*", "com.sun.org.apache.xpath.internal.jaxp"
                ),
                
            systemPackages(
                "com.sun.org.apache.xalan.internal.res",
                "com.sun.org.apache.xml.internal.utils", "com.sun.org.apache.xml.internal.utils",
                "com.sun.org.apache.xpath.internal", "com.sun.org.apache.xpath.internal.jaxp",
                "com.sun.org.apache.xpath.internal.objects"
                ),
            mavenBundle("org.glassfish", "javax.faces", "2.2.7"),
            mavenBundle("javax.servlet.jsp", "javax.servlet.jsp-api", "2.3.1"),
            mavenBundle("javax.servlet.jsp.jstl", "javax.servlet.jsp.jstl-api", "1.2.1"),
            mavenBundle("org.glassfish.web", "javax.servlet.jsp.jstl", "1.2.3"),
            mavenBundle("org.glassfish", "javax.el", "3.0.0"),
            mavenBundle("javax.enterprise", "cdi-api", "1.2"),
            mavenBundle("javax.interceptor", "javax.interceptor-api", "1.2"),
            mavenBundle("javax.validation", "validation-api", "1.1.0.Final"));            
    }
    
    public static Option workspaceBundle(String groupId, String artifactId) {
        String fileName = null;
        if (groupId.equals("org.ops4j.pax.cdi")) {
            fileName = String.format("/home/hwellmann/work/pax-cdi/%s/target/classes", artifactId);            
        }
        else {
            fileName = String.format("%s/../%s/target/classes", PathUtils.getBaseDir(), artifactId);
        }
        if (new File(fileName).exists()) {
            String url = "reference:file:" + fileName;
            return bundle(url);            
        }
        else {
            return mavenBundle(groupId, artifactId).versionAsInProject();
        }
    }
}
