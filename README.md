logback-journal
===============

[systemd](http://freedesktop.org/wiki/Software/systemd/) journal appender for Logback.

Installation
------------

This appender is published in sonatype maven repository. If you are using maven, simply add the following in your `pom.xml`
```xml
<dependency>
  <groupId>org.gnieh</groupId>
  <artifactId>logback-journal</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

if you are using sbt, add this to your `build.sbt`
```scala
libraryDependencies += "org.gnieh" % "logback-journal" % "0.1.0-SNAPSHOT"
```

Configuration
-------------

Basic configuration to use the systemd journal appender looks like this:
```xml
<configuration>

  <appender name="journal" class="org.gnieh.logback.SystemdJournalAppender" />

  <root level="debug">
    <appender-ref ref="journal" />
  </root>
</configuration>
```

The appender can be configured with the following properties

Property name      | Type    | Description | Default Value
------------------ | ------- | ----------- | -------------
`logLocation`      | boolean | Determines whether the exception locations are logged when present. This data is logged in standard systemd journal fields `CODE_FILE`, `CODE_LINE` and `CODE_FUNC`. | `true`
`logException`     | boolean | Determines whether the exception name and messages are logged. This data is logged in the user fields `EXN_NAME` and `EXN_MESSAGE`. | `true`
`logThreadName`    | boolean | Determines whether the thread name is logged. This data is logged in the user field `THREAD_NAME`. | `true`
`syslogIdentifier` | String  | Overrides the syslog identifier string. This data is logged in the user field `SYSLOG_IDENTIFIER`. | The process name (i.e. "java")

