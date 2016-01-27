/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.omnifone.commons.metrics.zabbix;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import io.github.hengyunabc.metrics.ZabbixReporter;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.web.ServletContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mkumar
 */
@WebListener
public class ZabbixMetricsReporter implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ZabbixMetricsReporter.class);
    private static final String METRIC_REGISTRY_NAME_PROPERTY = "com.omnifone.metrics.registry.name";
    private static final String METRIC_REGISTRY_NAME_DEFAULT = "OmnifoneMetrics";

    private static final String REPORT_SOURCE_USERNAME = "zabbix.metrics.source.username";
    private static final String REPORT_PERIOD = "zabbix.metrics.report.period.seconds";
    private static final int REPORT_PERIOD_SECS_DEFAULT = 60;
    private static final String HOSTNAME = "zabbix.metrics.hostname";
    private static final String PORT = "zabbix.metrics.port";
    private static final int PORT_DEFAULT = 10051;

    /**
     * Particular metrics are disabled by default but you can override this by specifying a comma
     * separated list of names
     */
    private static final String REPORT_DISABLED = "zabbix.metrics.disabled";

    private ZabbixReporter zabbixReporter;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOG.info("Starting ZabbixMetricsReporter");
        try {
            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new ServletContextConfiguration(event.getServletContext()));

            boolean reportSourceAsUserName = config.getBoolean(REPORT_SOURCE_USERNAME, false);
            if (reportSourceAsUserName) {
                LOG.info("Reporting source as username instead of default (hostname)");
            }

            // Set of reporting periods, by default only report at 5 minute level
            int reportPeriodSeconds = config.getInt(REPORT_PERIOD, REPORT_PERIOD_SECS_DEFAULT);

            String zabbixHostname = config.getString(HOSTNAME);
            int zabbixPort = config.getInt(PORT, PORT_DEFAULT);

            // default predicate is everything
            MetricFilter filter = MetricFilter.ALL;
            LOG.info("111Filter: " +filter);

            // but specific metrics can be disabled by specifying a regexp
            String metricsFilterDisabledRegex = config.getString(REPORT_DISABLED);
            if (metricsFilterDisabledRegex != null) {
                LOG.info("Excluding the reporting of matches to '{}' to zabbix. will report all others", metricsFilterDisabledRegex);
                filter = new RegexDisableMetricsFilter(metricsFilterDisabledRegex);
                LOG.info("#####Filetr : " +filter);
            } else {
                LOG.info("Reporting all metrics to zabbix");
            }


            if (zabbixHostname != null) {
                zabbixReporter = new ZabbixReporter.Builder(getRegistry(config))
                        .hostName(reportSourceAsUserName ? getUsername() : getSource())
                        .filter(filter).name("TestTimer")
                        //                .prefix("")
                        //                .replacePercentSign("")
                        .build(new ZabbixSender(zabbixHostname, zabbixPort));

                zabbixReporter.start(reportPeriodSeconds, TimeUnit.SECONDS);
                LOG.info("222zabbixReporter: " +zabbixReporter);
            } else {
                LOG.info("ZabbixMetricsReporter not created as Zabbix Hostname was null");
            }
        } catch (Throwable t) {
            LOG.error("Failed to initialize zabbix metrics reporter.", t);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (zabbixReporter != null) {
            zabbixReporter.stop();
        }
    }

    private static MetricRegistry getRegistry(Configuration config) {
        String registryName = null;

        try {
            registryName = config.getString(METRIC_REGISTRY_NAME_PROPERTY);
            LOG.info("***registryName: " +registryName);

        } catch (Exception e) {
            LOG.debug("Unable to retrieve metric registry name from config");
        }

        return SharedMetricRegistries.getOrCreate(
                registryName == null
                        ? METRIC_REGISTRY_NAME_DEFAULT
                        : registryName);

    }

    /**
     * Default source is the canonical host name. However, allow override.
     *
     * @return
     * @throws UnknownHostException
     */
    public String getSource() throws UnknownHostException {
        return InetAddress.getLocalHost().getCanonicalHostName();
    }

    private String getUsername() {
        return System.getProperty("user.name");
    }
}