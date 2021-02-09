# Jenkins Metricly Plugin

Jenkins plugin for publishing Jenkins statistics to Metricly.

## Installation

## Plugin Installation

- Go to the Jenkins Metricly Plugin releases page and download the `.hpi` file (https://github.com/metricly/jenkins-metricly-plugin/releases/latest)
- Go to the Advanced plugin manager page in your Jenkins installation (`/pluginManager/advanced`)
- Upload the `.hpi` file in the Upload Plugin section
- Restart Jenkins to finalize the install, then go to the Global Settings section below

### Global Settings

Once the plugin is installed:

- Go to `Manage Jenkins` in the left panel
- Click `Configure System`
- Scroll to `Metricly Plugin`
- Add your configuration (details below) and click Save

#### Configuration

- **Custom API Key** - Input your Metricly **CUSTOM** integration (found on https://try.cloudwisdom.virtana.com/) API key
- **Hostname** (default: Jenkins) - Element name for all metrics from this Jenkins installation
- **API Location** (default: https://try.cloudwisdom.virtana.com/) - Metricly API location

##### Advanced

- **Job Name Whitelist RegEx** - If not empty **only** jobs matching this RegEx will submit statistics to Metricly
- **Job Name Blacklist RegEx** - If not empty jobs matching this RegEx will **not** submit statistics to Metricly **even if they match the whitelist**

## Metrics

This plugin will create a single element (the hostname above will be used as the element name) with the following metrics:

- `jenkins.<job-name>.waiting` - Time (in ms) the job was waiting before starting the build
- `jenkins.<job-name>.duration` - Time (in ms) the job took to run
- `jenkins.<job-name>.completed` - Count of completed jobs
- `jenkins.<job-name>.success` - Count of successful jobs
- `jenkins.<job-name>.failure` - Count of failed jobs
