package com.omnifone.commons.metrics.zabbix;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author mkumar
 */

/**
 * Matches against names of metrics, filtering unwanted ones out and avoid publishing them.
 * This class provides regular expression based matching to
 * the actual reporter class.
 */


public class RegexDisableMetricsFilter implements MetricFilter {
    private static final Logger logger = LoggerFactory.getLogger(RegexDisableMetricsFilter.class);
    private final Pattern disablePattern;

    public RegexDisableMetricsFilter(String disableRegex) {
        logger.debug("Predicate: Disabled: [{}]", disableRegex);
        Objects.requireNonNull(disableRegex, "MetricsFilter Disable Regex can't be null");
        disablePattern = Pattern.compile(disableRegex);
    }

    @Override
    public boolean matches(String name, Metric metric) {
        logger.debug("Predicate.matches : timers[{}] Metric[{}]",
                name, metric.getClass());
        if (name == null || metric == null) {
            return false;
        }
        return !disablePattern.matcher(name).matches();

    }
}