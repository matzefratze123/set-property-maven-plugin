# set-property-maven-plugin
**Set project properties dynamically while building a maven project**

This plugin intends to help out other plugins by defining project properties via build-time.

Add the following to your pom to use the plugin:

```
<pluginRepositories>
  <repository>
    <id>matzes-repo</id>
    <url>http://matzefratze123.de/artifactory/release</url>
  </repository>
</pluginRepositories>
```

The plugin only provides one goal: set-properties

This goal evaluates a condition and decides based on the configuration which properties need to be set.


Example configuration:

```
<plugin>
  <groupId>de.matzefratze123</groupId>
  <artifactId>set-property-maven-plugin</artifactId>
  <version>1.0.1</version>
    <executions>
      <execution>
        <id>execution-id</id>
        <!-- Default phase is 'validate' if not specified -->
        <phase>validate</phase>
        <goals>
          <goal>set-properties</goal>
        </goals>
        <configuration>
          <operations>
            <operation>
              <!-- This is the condition for which it compares the string with another string -->
              <!-- In this example it compares the pom's version and checks wether it's a snapshot or not -->
              <condition>
                <string>${project.version}</string>
                <operator>EQUALS_REGEX</operator>
                <operand>.*-SNAPSHOT</operand>
              </condition>
              <!-- Define properties to be set when the condition above is true -->
              <setpropertiestrue>
                <setproperty>
                  <property>deploy.url</property>
                  <value>http://example.com/snapshots</value>
                </setproperty>
              </setpropertiestrue>
              <!-- Define properties to be set when the condition above is false -->
              <setpropertiesfalse>
                <setproperty>
                  <property>deploy.url</property>
                  <value>http://example.com/releases</value>
                </setproperty>
              </setpropertiesfalse>
            </operation>
          </operations>
          <extractions>
            <extraction>
              <!--
              Control maven deployment to deploy snapshot, version based on gitlab build number or skip deployment
              Extracts XXX from [DEPLOY XXX] which should be should be one of VERSION, SNAPSHOT, SKIP
              just [DEPLOY] without XX means [DEPLOY VERSION]
              If no [DEPLOY XXX] tag is present, defaulting to SNAPSHOT maven deployment build
              -->
              <property>buildFlag.deployment</property>
              <regex>.*\[DEPLOY\s+(VERSION|SNAPSHOT|SKIP)+].*|.*\[(DEPLOY)+].</regex>
              <text>${git.commit.message.full}</text>
              <caseSensitive>false</caseSensitive>
              <toLowerCase>true</toLowerCase>
              <defaultValue>SNAPSHOT</defaultValue>
            </extraction>
          </extractions>
        </configuration>
      </execution>
    </executions>    
  </plugin>
  ```
