package toto;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@RunWith(Arquillian.class)
public class Test {

    @Deployment
    public static Archive myApp() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addPackage(Test.class.getPackage())
                .addResource("META-INF/beans.xml", "beans.xml");

        return archive;
    }

    @Inject @Any
    Instance<Object> instance;

    @org.junit.Test
    public void test() {
        Object service = null;
        Annotation[] annotations = new Annotation[1];
        annotations[0] = ServiceImpl.class.getAnnotation(Name.class);
        service = instance.select(Service.class,annotations).get();
        System.out.println(service);
    }
}
