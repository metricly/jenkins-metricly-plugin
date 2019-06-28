package com.metricly.jenkins.plugins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

@Extension
public class MetriclyBuildListener extends RunListener<Run> implements Describable<MetriclyBuildListener> {

    static final String DISPLAY_NAME = "Metricly Plugin";
    static final String DEFAULT_API_LOCATION = "https://api.app.metricly.com";

    private static final Logger logger =  Logger.getLogger(MetriclyBuildListener.class.getName());

    public final void onStarted(final Run run, final TaskListener listener) {
        String jobName = run.getParent().getFullName();
        logger.info(String.format("onStarted() called with jobName: %s", jobName));
    }

    public final void onCompleted(final Run run, final TaskListener listener) {
        String jobName = run.getParent().getFullName();
        logger.info(String.format("onCompleted() called with jobName: %s", jobName));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<MetriclyBuildListener> {

        private Secret apiKey = Secret.fromString("");
        private String hostname;
        private String apiLocation = DEFAULT_API_LOCATION;

        public DescriptorImpl() {
            load();
        }

        @Override
        public MetriclyBuildListener newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData); //To change body of generated methods, choose Tools | Templates.
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return MetriclyBuildListener.DISPLAY_NAME;
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            this.apiKey = Secret.fromString(formData.getString("apiKey"));
            logger.info(String.format("Configuring Metricly plugin with API key: %s", apiKey));

            this.hostname = formData.getString("hostname");
            logger.info(String.format("Configuring Metricly plugin with hostname: %s", hostname));

            if (StringUtils.isNotBlank(formData.getString("apiLocation"))) {
                this.apiLocation = formData.getString("apiLocation");
                logger.info(String.format("Configuring Metricly plugin with API location: %s", apiLocation));
            } else {
                this.apiLocation = DEFAULT_API_LOCATION;
            }

            save();
            return super.configure(req, formData);
        }

        public Secret getApiKey() {
            return apiKey;
        }

        public String getHostname() {
            return hostname;
        }

        public String getApiLocation() {
            return apiLocation;
        }
    }
}
