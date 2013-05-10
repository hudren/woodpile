# Woodpile

Woodpile is an Eclipse plugin for capturing and viewing Apache log4j events from multiple sources.

NB: Woodpile is a developer tool and not meant for monitoring production servers.

## Requirements

Woodpile requires a minimum of Eclipse 3.2 running on Java 6.

## Update Site

The update site for Woodpile is `http://www.hudren.com/update`.

## Log4j Configuration

Each log source must be configured to log events using the standard Log4j SocketAppender, for example:

<pre><code>&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;!DOCTYPE log4j:configuration SYSTEM "log4j.dtd"&gt;

&lt;log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/"&gt;
	&lt;appender name="console" class="org.apache.log4j.ConsoleAppender"&gt;
		&lt;param name="Target" value="System.out" /&gt;
		&lt;layout class="org.apache.log4j.PatternLayout"&gt;
			&lt;param name="ConversionPattern" value="%-5p %c{1} - %m%n" /&gt;
		&lt;/layout&gt;
	&lt;/appender&gt;

	&lt;appender name="woodpile" class="org.apache.log4j.net.SocketAppender"&gt;
		&lt;param name="application" value="hello" /&gt;
		&lt;param name="remoteHost" value="localhost" /&gt;
		&lt;param name="port" value="4560" /&gt;
	&lt;/appender&gt;

	&lt;appender name="async" class="org.apache.log4j.AsyncAppender"&gt;
		&lt;appender-ref ref="console" /&gt;
		&lt;appender-ref ref="woodpile" /&gt;
	&lt;/appender&gt;

	&lt;root&gt;
		&lt;level value="debug" /&gt;
		&lt;appender-ref ref="async" /&gt;
	&lt;/root&gt;
&lt;/log4j:configuration&gt;
</code></pre>


The `remoteHost` and `port` parameters for the SocketAppender are used to communicate with Woodpile by sending events across the TCP/IP stack. The events are identified for display within Woodpile by the `application` parameter.

## License

Copyright &copy; 2006-2013 Hudren Andromeda Connection. All rights reserved.

Distributed under the [Eclipse Public License 1.0](http://opensource.org/licenses/eclipse-1.0.php).
