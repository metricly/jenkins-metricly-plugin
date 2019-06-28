package com.metricly.jenkins.plugins;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import java.util.logging.Logger;

@Extension
public class MetriclyBuildListener extends RunListener<Run> implements Describable<MetriclyBuildListener> {

    private static final Logger logger =  Logger.getLogger(MetriclyBuildListener.class.getName());

    public final void onStarted(final Run run, final TaskListener listener) {
        String jobName = run.getParent().getFullName();
        logger.info(String.format("onStarted() called with jobName: %s", jobName));
    }

    @Override
    public Descriptor<MetriclyBuildListener> getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<MetriclyBuildListener> {

    }
}
