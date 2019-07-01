package com.metricly.jenkins.plugins;

import com.metricly.jenkins.plugins.client.IngestPayload;
import com.metricly.jenkins.plugins.client.MetriclyClient;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Queue;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

@Extension
public class MetriclyBuildListener extends RunListener<Run> implements Describable<MetriclyBuildListener> {

    static final String DISPLAY_NAME = "Metricly Plugin";
    static final String DEFAULT_HOSTNAME = "Jenkins";
    static final String DEFAULT_API_LOCATION = "https://api.app.metricly.com";

    private static final Queue queue = Queue.getInstance();

    private static final Logger logger =  Logger.getLogger(MetriclyBuildListener.class.getName());

    public final void onStarted(final Run run, final TaskListener listener) {
        String jobName = run.getParent().getFullName();
        logger.info(String.format("onStarted() called with jobName: %s", jobName));

        if (!getDescriptor().shouldCollectForJob(jobName)) {
            logger.info(String.format("Job %s is either blacklisted or not whitelisted, skipping", jobName));
            return;
        }

        Queue.Item item = queue.getItem(run.getQueueId());

        IngestPayload ingestPayload = new IngestPayload(getDescriptor().getHostname());

        try {
            ingestPayload.addSample(jobName, "waiting", (System.currentTimeMillis() - item.getInQueueSince()) / 1000L);
        } catch (Exception e) {
            logger.warning("Unable to compute 'waitin' time.");
        }

        getDescriptor().getClient().submit(ingestPayload);
        logger.info(String.format("Finished onStarted() for jobName: %s", jobName));
    }

    public final void onCompleted(final Run run, final TaskListener listener) {
        String jobName = run.getParent().getFullName();
        logger.info(String.format("onCompleted() called with jobName: %s", jobName));

        if (!getDescriptor().shouldCollectForJob(jobName)) {
            logger.info(String.format("Job %s is either blacklisted or not whitelisted, skipping", jobName));
            return;
        }

        IngestPayload ingestPayload = new IngestPayload(getDescriptor().getHostname());

        ingestPayload.addSample(jobName, "duration", run.getDuration());
        ingestPayload.addSample(jobName, "completed", 1);
        if (run.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
            ingestPayload.addSample(jobName, "success", 1);
        } else if (run.getResult().isWorseThan(Result.UNSTABLE)) {
            ingestPayload.addSample(jobName, "failure", 1);
        }

        getDescriptor().getClient().submit(ingestPayload);
        logger.info(String.format("Finished onCompleted() for jobName: %s", jobName));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<MetriclyBuildListener> {

        private String apiKey;
        private String hostname = DEFAULT_HOSTNAME;
        private String apiLocation = DEFAULT_API_LOCATION;
        private String jobWhitelist;
        private String jobBlacklist;

        private MetriclyClient client;

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
            this.apiKey = formData.getString("apiKey");
            logger.info(String.format("Configuring Metricly plugin with API key: %s", apiKey));

            if (StringUtils.isNotBlank(formData.getString("hostname"))) {
                this.hostname = formData.getString("hostname");
                logger.info(String.format("Configuring Metricly plugin with hostname: %s", hostname));
            } else {
                this.hostname = DEFAULT_HOSTNAME;
            }

            if (StringUtils.isNotBlank(formData.getString("apiLocation"))) {
                this.apiLocation = formData.getString("apiLocation");
                logger.info(String.format("Configuring Metricly plugin with API location: %s", apiLocation));
            } else {
                this.apiLocation = DEFAULT_API_LOCATION;
            }

            this.jobWhitelist = formData.getString("jobWhitelist");
            logger.info(String.format("Configuring Metricly plugin with job name whitelist: %s", jobWhitelist));
            this.jobBlacklist = formData.getString("jobBlacklist");
            logger.info(String.format("Configuring Metricly plugin with job name blacklist: %s", jobBlacklist));

            save();
            return super.configure(req, formData);
        }

        public boolean shouldCollectForJob(String jobName) {
            if (StringUtils.isNotBlank(jobBlacklist) && jobName.matches(jobBlacklist)) {
                return false;
            }
            if (StringUtils.isNotBlank(jobWhitelist)) {
                return jobName.matches(jobWhitelist);
            }
            return true;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getHostname() {
            return hostname;
        }

        public String getApiLocation() {
            return apiLocation;
        }

        public String getJobWhitelist() {
            return jobWhitelist;
        }

        public String getJobBlacklist() {
            return jobBlacklist;
        }

        public MetriclyClient getClient() {
            if (client == null) {
                client = new MetriclyClient(apiKey, apiLocation);
            }
            return client;
        }
    }
}
