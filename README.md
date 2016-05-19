# ops-asg-metrics-zabbix-servlet
This is a small servlet project for metrics in order to report measures to Zabbix.

## Import with Maven: ##
    <dependency>
         <groupId>com.omnifone.commons.metrics</groupId>
         <artifactId>ops-asg-metrics-zabbix-servlet</artifactId>
         <version>1.0.0</version>
    </dependency>
    

## Configuration in Tomcat

| Configuration parameter | Description | Example |
| --- | ----- | --- |
| -Dzabbix.metrics.disabled | To disable any metrics API(s), if you need to pass more than one APIs please use with comma (,) | 	-Dzabbix.metrics.disabled=mss.gateway.api.getArtist.requests |
| -Dzabbix.metrics.hostname |	To pass hostname |	-Dzabbix.metrics.hostname=< hostname > |
| -Dzabbix.metrics.port | zabbix port, by default is set as 10051	| -Dzabbix.metrics.port=10051 |
| -Dzabbix.metrics.report.period.seconds |	By default report period is set as 60s, use this configuration if you need to change period, values are in seconds.	| -Dzabbix.metrics.report.period.seconds=60 |
| -Dzabbix.metrics.source.username |	To pass any specific user name	| -Dzabbix.metrics.source.username=UserName |
