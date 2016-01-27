package com.omnifone.commons.metrics.zabbix;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Pattern disablePattern = null;

    /**
     * Convenience constructor. Expects a regular expression that indicates which
     * metrics to disable.
     */

    public RegexDisableMetricsFilter(String disableRegex) {
        logger.debug("Predicate: Disabled: [{}]", disableRegex);
       // Objects.requireNonNull(disableRegex, "MetricsFilter Disable Regex can't be null");
        if (disableRegex != null && !disableRegex.trim().isEmpty()) {
            disablePattern = Pattern.compile(disableRegex);
        }
    }

    /**
     * Matches returns true unless the metric name does not match the regular
     * expression passed to the constructor when creating the predicate matcher.
     * @return True if the regular expression matches the metric name
     */
    @Override
    public boolean matches(String name, Metric metric) {
        if (name == null || metric == null) {
            return false;
        }

        logger.debug("Predicate.matches : Name[{}] Metric[{}]",
                name, metric.getClass());

        boolean retVal = true;
        if (disablePattern != null) {

            //Creating JSON object
            JSONObject metricsJsonObj = new JSONObject();
            metricsJsonObj.put("Name", name);
            logger.debug("Name JSON object: " +name);
                    metricsJsonObj.put("Metrics", metric);
            logger.debug("Name JSON Metrics: " +metric);

            // If it matches, don't send. Otherwise, send.
           // retVal = !disablePattern.matcher(name).matches();

            retVal = !disablePattern.matcher(metricsJsonObj.toJSONString()).matches();
            logger.debug("JSON Object: " +metricsJsonObj);
            System.out.println("JSON Object: " +metricsJsonObj);
        }

        logger.debug("Predicate.matches: Name[{}] Metric[{}]: [{}]",
                name, metric.getClass(), retVal);

        return retVal;
    }
}
