/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.omnifone.commons.metrics.zabbix;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author sandygordon
 */
public class RegexDisableMetricsFilter implements MetricFilter {

    private final Pattern disablePattern;

    public RegexDisableMetricsFilter(String disableRegex) {
        Objects.requireNonNull(disableRegex, "MetricsFilter Disable Regex can't be null");
        disablePattern = Pattern.compile(disableRegex);
    }

    @Override
    public boolean matches(String name, Metric metric) {
        if (name == null || metric == null) {
            return false;
        }
        return !disablePattern.matcher(name).matches();
    }
}