package ${package};

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.BundleContainerShutdown;

@ApplicationScoped
public class App {

    public void start(@Observes BundleContainerInitialized init) {
        System.out.println("Hello World !");
    }

    public void stop(@Observes BundleContainerShutdown init) {
        System.out.println("Bye bye World !");
    }
}