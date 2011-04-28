package org.apache.openwebbeans.environment.osgi.integration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.webbeans.exception.WebBeansDeploymentException;
import org.apache.webbeans.spi.BDABeansXmlScanner;
import org.apache.webbeans.spi.ScannerService;
import org.osgi.framework.Bundle;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiScanner implements ScannerService {

    /** All classes which have to be scanned for Bean information */
    private Set<Class<?>> beanClasses = new HashSet<Class<?>>();
    /** the paths of all META-INF/beans.xml files */
    private Set<String> beanXMLs = new HashSet<String>();
    /**contains all the JARs we found with valid beans.xml in it */
    private Set<String> beanArchiveJarNames = new HashSet<String>();
    private Map<String, Set<String>> classAnnotations = new HashMap<String, Set<String>>();
    private Bundle bundle;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void init(Object object) {
//        if (object instanceof ServletContext) {
//            servletContext = (ServletContext) object;
//        }
    }

    @Override
    public void release() {
//        beanClasses = new HashSet<Class<?>>();
//        beanXMLs = new HashSet<String>();
//        beanArchiveJarNames = new HashSet<String>();
//        classAnnotations.clear();
    }

    public void release2() {
        beanClasses = new HashSet<Class<?>>();
        beanXMLs = new HashSet<String>();
        beanArchiveJarNames = new HashSet<String>();
        classAnnotations.clear();
    }

    @Override
    public void scan() throws WebBeansDeploymentException {
        if (bundle == null) {
            throw new RuntimeException("Bundle can't be null");
        }
        // TODO : use BundleScanner API
        Enumeration beansXml = bundle.findEntries("META-INF", "beans.xml", true);
        if (beansXml != null) {
            while (beansXml.hasMoreElements()) {
                URL url = ((URL) beansXml.nextElement());
                beanXMLs.add(url.toString());
            }
            Enumeration foundBeanClasses = bundle.findEntries("", "*.class", true);
            if (foundBeanClasses != null) {
                while (foundBeanClasses.hasMoreElements()) {
                    URL url = (URL) foundBeanClasses.nextElement();
                    String clazz = url.getFile().substring(1).replace("/", ".").replace(".class", "");
                    try {
                        beanClasses.add(bundle.loadClass(clazz));
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
            for (Class<?> clazz : beanClasses) {
                Set<String> set = collectAnnotations(clazz);
                if (!classAnnotations.containsKey(clazz.getName())) {
                    classAnnotations.put(clazz.getName(), new HashSet<String>());
                }
                classAnnotations.get(clazz.getName()).addAll(set);
            }
        }
    }

    private Set<String> collectAnnotations(Class<?> cls) {
        Set<String> annotations = new HashSet<String>();

        addAnnotations(annotations, cls.getAnnotations());

        Constructor[] constructors = cls.getDeclaredConstructors();
        for (Constructor c : constructors) {
            addAnnotations(annotations, c.getAnnotations());
        }

        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            addAnnotations(annotations, f.getAnnotations());
        }

        Method[] methods = cls.getDeclaredMethods();
        for (Method m : methods) {
            addAnnotations(annotations, m.getAnnotations());

            Annotation[][] paramsAnns = m.getParameterAnnotations();
            for (Annotation[] pAnns : paramsAnns) {
                addAnnotations(annotations, pAnns);
            }
        }
        return annotations;
    }

    private void addAnnotations(Set<String> annStrings, Annotation[] annotations) {
        for (Annotation ann : annotations) {
            annStrings.add(ann.getClass().getSimpleName());
        }
    }

    @Override
    public Set<String> getBeanXmls() {
        return beanXMLs;
    }

    @Override
    public Set<Class<?>> getBeanClasses() {
        return beanClasses;
    }

    @Override
    public Set<String> getAllAnnotations(String className) {
        return classAnnotations.get(className);
    }

    @Override
    public BDABeansXmlScanner getBDABeansXmlScanner() {
        return null;
    }

    @Override
    public boolean isBDABeansXmlScanningEnabled() {
        return false;
    }
}
