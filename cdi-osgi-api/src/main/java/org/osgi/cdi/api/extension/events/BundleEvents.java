package org.osgi.cdi.api.extension.events;

import org.osgi.framework.Bundle;

/**
 *
 * @author mathieuancelin
 */
public class BundleEvents {

    public static class BundleInstalled extends AbstractBundleEvent {

        public BundleInstalled(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.INSTALLED;
        }
    }

    public static class BundleLazyActivation extends AbstractBundleEvent {

        public BundleLazyActivation(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.LAZY_ACTIVATION;
        }
    }

    public static class BundleResolved extends AbstractBundleEvent {

        public BundleResolved(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.RESOLVED;
        }
    }

    public static class BundleStarted extends AbstractBundleEvent {

        public BundleStarted(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.STARTED;
        }
    }

    public static class BundleStarting extends AbstractBundleEvent {

        public BundleStarting(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.STARTING;
        }
    }

    public static class BundleStopped extends AbstractBundleEvent {

        public BundleStopped(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.STOPPED;
        }
    }

    public static class BundleStopping extends AbstractBundleEvent {

        public BundleStopping(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.STOPPING;
        }
    }

    public static class BundleUninstalled extends AbstractBundleEvent {

        public BundleUninstalled(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.UNINSTALLED;
        }
    }

    public static class BundleUnresolved extends AbstractBundleEvent {

        public BundleUnresolved(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.UNRESOLVED;
        }
    }

    public static class BundleUpdated extends AbstractBundleEvent {

        public BundleUpdated(Bundle bundle) {
            super(bundle);
        }

        @Override
        public EventType getType() {
            return EventType.UPDATED;
        }
    }
}
