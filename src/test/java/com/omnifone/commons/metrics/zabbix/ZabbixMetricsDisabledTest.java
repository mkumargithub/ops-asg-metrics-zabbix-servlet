package com.omnifone.commons.metrics.zabbix;

import com.codahale.metrics.Metric;
import com.omnifone.commons.metrics.zabbix.RegexDisableMetricsFilter;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mkumar
 */
public class ZabbixMetricsDisabledTest extends TestCase {

    private class TestMetric implements Metric {
    }

    private static final Logger logger = LoggerFactory.getLogger(ZabbixMetricsDisabledTest.class);

    @Override
    protected void setUp() throws Exception {
        System.out.println("setUp");
    }

    @Test
    public void testMatchesRegularExpression() {
        System.out.println("testMatchesRegularExpression");

        String disabled1 = "jvm.heap.size";
        String disabled2 = "jvm.memory.total.init";
        String disabled3 = "jvm.thread-states.count";
        String disabled4 = "jvm.thread-states.daemon.count";
        
        // We don't want to send thread-states nor memory.total
        RegexDisableMetricsFilter instance
                = new RegexDisableMetricsFilter("(thread-states)|(memory\\.total)");

        // Check instance.matches
        Metric testMetric = new TestMetric();
        assertTrue(disabled1 + " should be published to zabbix",
                instance.matches(disabled1, testMetric));
        
        assertFalse(disabled2 + " should not be published to zabbix",
                instance.matches(disabled2, testMetric));
        
        assertFalse(disabled3 + " should not be published to zabbix",
                instance.matches(disabled3, testMetric));
        
        assertFalse(disabled4 + " should not be published to zabbix",
                instance.matches(disabled4, testMetric));

        // test null input doesn't break anything
        assertFalse("Null", instance.matches(null, testMetric));
        assertFalse("Null", instance.matches(disabled3, null));
        assertFalse("Null", instance.matches(null, null));
    }

    @Test
    public void testMatchesRegularExpressionWithGroupAndClass() {
        System.out.println("testMatchesRegularExpressionWithGroupAndClass");

        String disabled1 = "group1.Class.jvm.heap.size";
        String disabled2 = "group1.Class.jvm.memory.total.init";
        String disabled3 = "group2.Class.jvm.thread-states.count";
        String disabled4 = "group3.Class.jvm.thread-states.daemon.count";
        
        // We don't want to send thread-states nor memory.total
        RegexDisableMetricsFilter instance
                = new RegexDisableMetricsFilter("group1\\.Class");

        // Check instance.matches
        Metric testMetric = new TestMetric();
        assertFalse("Group " + disabled1 + " should be published to zabbix",
                instance.matches(disabled1, testMetric));
        
        assertFalse("Group " + disabled2 + " should not be published to zabbix",
                instance.matches(disabled2, testMetric));
        
        assertTrue("Group " + disabled3 + " should not be published to zabbix",
                instance.matches(disabled3, testMetric));
        
        assertTrue("Group " + disabled4 + " should not be published to zabbix",
                instance.matches(disabled4, testMetric));

        // test null input doesn't break anything
        assertFalse("Null", instance.matches(null, testMetric));
        assertFalse("Null", instance.matches(disabled3, null));
        assertFalse("Null", instance.matches(null, null));
    }
    
    @Test
    public void testMatchesRegularExpressionNull() {
        System.out.println("testMatchesRegularExpressionNull");

        String disabled1 = "jvm.memory.total.init";
        String disabled2 = "jvm.thread-states.count";
        String disabled3 = "jvm.thread-states.daemon.count";
        RegexDisableMetricsFilter instance = new RegexDisableMetricsFilter(null);

        // check instance.matches
        // all should match as the disabled list is empty
        Metric testMetric = new TestMetric();
        assertTrue(disabled1, instance.matches(disabled1, testMetric));
        assertTrue(disabled2, instance.matches(disabled2, testMetric));
        assertTrue(disabled3, instance.matches(disabled3, testMetric));

        // test null input doesn't break anything
        // test null input doesn't break anything
        assertFalse("Null", instance.matches(null, testMetric));
        assertFalse("Null", instance.matches(disabled3, null));
        assertFalse("Null", instance.matches(null, null));
    }

    @Test
    public void testMatchesRegularExpressionEmpty() {
        System.out.println("testMatchesRegularExpressionEmpty");

        String disabled1 = "jvm.memory.total.init";
        String disabled2 = "jvm.thread-states.count";
        String disabled3 = "jvm.thread-states.daemon.count";
        RegexDisableMetricsFilter instance = new RegexDisableMetricsFilter("");

        // check instance.matches
        Metric testMetric = new TestMetric();
        assertTrue(disabled1, instance.matches(disabled1, testMetric));
        assertTrue(disabled2, instance.matches(disabled2, testMetric));
        assertTrue(disabled3, instance.matches(disabled3, testMetric));

        // test null input doesn't break anything
        assertFalse("Null", instance.matches(null, testMetric));
        assertFalse("Null", instance.matches(disabled3, null));
        assertFalse("Null", instance.matches(null, null));
    }
}