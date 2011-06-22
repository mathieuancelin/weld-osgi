package com.sample.osgi.bundle1.util;

import com.sample.osgi.bundle1.api.MovingService;
import com.sample.osgi.bundle1.impl.MovingServiceImpl;
import org.osgi.cdi.api.extension.annotation.Publish;
import org.osgi.cdi.api.extension.events.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Publish
@ApplicationScoped
public class EventListener {

    public MovingService getMovingServiceInstance() {
        return new MovingServiceImpl();
    }

    private int start = 0;
    private int stop = 0;

    private void start(@Observes BundleContainerEvents.BundleContainerInitialized event) {
        start++;
    }

    private void stop(@Observes BundleContainerEvents.BundleContainerShutdown event) {
        stop++;
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

    public int getBundleUninstalled() {
        return bundleUninstalled;
    }

    public int getBundleResolved() {
        return bundleResolved;
    }

    public int getBundleUnresolved() {
        return bundleUnresolved;
    }

    public int getBundleStarting() {
        return bundleStarting;
    }

    public int getBundleStarted() {
        return bundleStarted;
    }

    public int getBundleStopping() {
        return bundleStopping;
    }

    public int getBundleStopped() {
        return bundleStopped;
    }

    public int getBundleUpdated() {
        return bundleUpdated;
    }

    public int getBundleLazyActivation() {
        return bundleLazyActivation;
    }

    private int bundleValid = 0;
    private int bundleInvalid = 0;

    private void bundleValid(@Observes Valid event) {
        bundleValid++;
    }

    private void bundleInvalid(@Observes Invalid event) {
        bundleInvalid++;
    }

    public int getBundleValid() {
        return bundleValid;
    }

    public int getBundleInvalid() {
        return bundleInvalid;
    }
}
