## fmt-maven-plugin

Formats your code following [google-java-format](https://github.com/google/google-java-format) which follows [Google's code styleguide](https://google.github.io/styleguide/javaguide.html).

The format cannot be configured by design.

If you want your IDE to stick to the same format, check out [the available configuration files](https://github.com/google/styleguide)

* [Eclipse](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)
* [IntelliJ IDEA](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)

## Usage

### Standard pom.xml

Add to your pom.xml

```
    <build>
        <plugins>
            <plugin>
                <groupId>com.coveo</groupId>
                <artifactId>fmt-maven-plugin</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>format</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
``` 

`sourceDirectory` represents the directory where your Java sources that need to be formatted are contained. It defaults to `${project.build.sourceDirectory}`

`testSourceDirectory` represents the directory where your test's Java sources that need to be formatted are contained. It defaults to `${project.build.testSourceDirectory}`

`additionalSourceDirectories` represents a list of additional directories that contains Java sources that need to be formatted. It defaults to an empty list.

`verbose` is whether the plugin should print a line for every file that is being formatted. It defaults to `false`.

### Configured pom.xml


pom.xml with sample configuration:

```
    <build>
        <plugins>
            <plugin>
                <groupId>com.coveo</groupId>
                <artifactId>fmt-maven-plugin</artifactId>
                <version>1.0</version>
                <configuration>
                    <sourceDirectory>some/source/directory</sourceDirectory>
                    <testSourceDirectory>some/test/directory</testSourceDirectory>
                    <verbose>true</verbose>
                    <additionalSourceDirectories>
                        <param>some/dir</param>
                        <param>some/other/dir</param>
                    </additionalSourceDirectories>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>format</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
``` 

### Command line

You can also use it on the command line

`mvn com.coveo:fmt-maven-plugin:format`

You can pass parameters via standard `-D` syntax.
`mvn com.coveo:fmt-maven-plugin:format -Dverbose=true`