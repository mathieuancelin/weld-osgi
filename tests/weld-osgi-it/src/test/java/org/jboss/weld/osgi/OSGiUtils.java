package org.jboss.weld.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class OSGiUtils {

    public static void waitForActiveBundle(BundleContext context, String bundleSymbolicName) throws Exception {
        boolean present = false;
        while (!present) {
            for (Bundle b : context.getBundles()) {
                if (b.getSymbolicName().equals(bundleSymbolicName)) {
                    if (b.getState() == Bundle.ACTIVE) {
                        present = true;
                    }
                }
            }
            Thread.sleep(1000);
        }
    }
}
