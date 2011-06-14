package com.sample.osgi.bundle1.util;

import org.osgi.cdi.api.extension.annotation.Publish;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;
import org.osgi.cdi.api.extension.events.BundleEvents;
import org.osgi.cdi.api.extension.events.ServiceEvents;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Publish
@ApplicationScoped
public class EventListener {

//    @Inject
//    @OSGiService
//    Bundle1Listener listener;

    private int start = 0;
    private int stop = 0;

    private void start(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        start++;
    }

    private void stop(@Observes BundleContainerEvents.BundleContainerShutdown event) {
        stop++;
//        listener.setStop(stop);
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    private int serviceArrival = 0;
    private int serviceChanged = 0;
    private int serviceDeparture = 0;

    private void serviceArrival(@Observes ServiceEvents.ServiceArrival event) {
        serviceArrival++;
    }

    private void serviceChanged(@Observes ServiceEvents.ServiceChanged event) {
        serviceChanged++;
    }

    private void serviceDeparture(@Observes ServiceEvents.ServiceDeparture event) {
        serviceDeparture++;
    }

    public int getServiceArrival() {
        return serviceArrival;
    }

    public int getServiceChanged() {
        return serviceChanged;
    }

    public int getServiceDeparture() {
        return serviceDeparture;
    }

    private int bundleInstalled = 0;
    private int bundleUninstalled = 0;
    private int bundleResolved = 0;
    private int bundleUnresolved = 0;
    private int bundleStarting = 0;
    private int bundleStarted = 0;
    private int bundleStopping = 0;
    private int bundleStopped = 0;
    private int bundleUpdated = 0;
    private int bundleLazyActivation = 0;

    private void bundleInstalled (@Observes BundleEvents.BundleInstalled event) {
        bundleInstalled++;
    }

    private void bundleUninstalled (@Observes BundleEvents.BundleUninstalled event) {
        bundleUninstalled++;
    }

    private void bundleResolved (@Observes BundleEvents.BundleResolved event) {
        bundleResolved++;
    }

    private void bundleUnresolved (@Observes BundleEvents.BundleUnresolved event) {
        bundleUnresolved++;
    }

    private void bundleStarting (@Observes BundleEvents.BundleStarting event) {
        bundleStarting++;
    }

    private void bundleStarted (@Observes BundleEvents.BundleStarted event) {
        bundleStarted++;
    }

    private void bundleStopping (@Observes BundleEvents.BundleStopping event) {
        bundleStopping++;
    }

    private void bundleStopped (@Observes BundleEvents.BundleStopped event) {
        bundleStopped++;
    }

    private void bundleUpdated (@Observes BundleEvents.BundleUpdated event) {
        bundleUpdated++;
    }

    private void bundleLazyActivation (@Observes BundleEvents.BundleLazyActivation event) {
        bundleLazyActivation++;
    }

    public int getBundleInstalled() {
        return bundleInstalled;
    }

    public void setBundleInstalled(int bundleInstalled) {
        this.bundleInstalled = bundleInstalled;
    }

    public int getBundleUninstalled() {
        return bundleUninstalled;
    }

    public void setBundleUninstalled(int bundleUninstalled) {
        this.bundleUninstalled = bundleUninstalled;
    }

    public int getBundleResolved() {
        return bundleResolved;
    }

    public void setBundleResolved(int bundleResolved) {
        this.bundleResolved = bundleResolved;
    }

    public int getBundleUnresolved() {
        return bundleUnresolved;
    }

    public void setBundleUnresolved(int bundleUnresolved) {
        this.bundleUnresolved = bundleUnresolved;
    }

    public int getBundleStarting() {
        return bundleStarting;
    }

    public void setBundleStarting(int bundleStarting) {
        this.bundleStarting = bundleStarting;
    }

    public int getBundleStarted() {
        return bundleStarted;
    }

    public void setBundleStarted(int bundleStarted) {
        this.bundleStarted = bundleStarted;
    }

    public int getBundleStopping() {
        return bundleStopping;
    }

    public void setBundleStopping(int bundleStopping) {
        this.bundleStopping = bundleStopping;
    }

    public int getBundleStopped() {
        return bundleStopped;
    }

    public void setBundleStopped(int bundleStopped) {
        this.bundleStopped = bundleStopped;
    }

    public int getBundleUpdated() {
        return bundleUpdated;
    }

    public void setBundleUpdated(int bundleUpdated) {
        this.bundleUpdated = bundleUpdated;
    }

    public int getBundleLazyActivation() {
        return bundleLazyActivation;
    }

    public void setBundleLazyActivation(int bundleLazyActivation) {
        this.bundleLazyActivation = bundleLazyActivation;
    }
}
