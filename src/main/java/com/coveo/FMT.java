package com.coveo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FMT extends AbstractMojo {
  private Log logger = getLog();
  private Formatter formatter = new Formatter();

  @Parameter(
    defaultValue = "${project.build.sourceDirectory}",
    property = "sourceDirectory",
    required = true
  )
  private File sourceDirectory;

  @Parameter(
    defaultValue = "${project.build.testSourceDirectory}",
    property = "testSourceDirectory",
    required = true
  )
  private File testSourceDirectory;

  @Parameter(property = "additionalSourceDirectories")
  private File[] additionalSourceDirectories;

  @Parameter(defaultValue = "false", property = "verbose")
  private boolean verbose;

  @Parameter(defaultValue = "false", property = "failOnUnknownFolder")
  private boolean failOnUnknownFolder;

  private List<String> filesFormatted = new ArrayList<String>();

  public void execute() throws MojoExecutionException, MojoFailureException {
    List<File> directoriesToFormat = new ArrayList<File>();
    if (sourceDirectory.exists()) {
      directoriesToFormat.add(sourceDirectory);
    } else {
      handleMissingDirectory("Source", sourceDirectory);
    }
    if (testSourceDirectory.exists()) {
      directoriesToFormat.add(testSourceDirectory);
    } else {
      handleMissingDirectory("Test source", testSourceDirectory);
    }

    for (File additionalSourceDirectory : additionalSourceDirectories) {
      if (additionalSourceDirectory.exists()) {
        directoriesToFormat.add(additionalSourceDirectory);
      } else {
        handleMissingDirectory("Additional source", additionalSourceDirectory);
      }
    }

    for (File directoryToFormat : directoriesToFormat) {
      formatSourceFilesInDirectory(directoryToFormat);
    }

    logNumberOfFilesFormatted();
  }

  public List<String> getFilesFormatted() {
    return filesFormatted;
  }

  private void formatSourceFilesInDirectory(File directory) {
    if (!directory.isDirectory()) {
      logger.info("Directory '" + directory + "' is not a directory. Skipping.");
      return;
    }

    List<File> files = Arrays.asList(directory.listFiles());
    for (File file : files) {
      if (file.isDirectory()) {
        formatSourceFilesInDirectory(file);
      } else {
        formatSourceFile(file);
      }
    }
  }

  private void formatSourceFile(File file) {
    if (file.isDirectory()) {
      logger.info("File '" + file + "' is a directory. Skipping.");
      return;
    }

    if (verbose) {
      logger.debug("Formatting '" + file + "'.");
    }

    CharSource source = Files.asCharSource(file, Charsets.UTF_8);
    CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
    try {
      formatter.formatSource(source, sink);
      filesFormatted.add(file.getAbsolutePath());
      if (filesFormatted.size() % 100 == 0) {
        logNumberOfFilesFormatted();
      }
    } catch (FormatterException e) {
      logger.warn("Failed to format file '" + file + "'.", e);
    } catch (IOException e) {
      logger.warn("Failed to format file '" + file + "'.", e);
    }
  }

  private void handleMissingDirectory(String directoryDisplayName, File directory)
      throws MojoFailureException {
    if (failOnUnknownFolder) {
      String message =
          directoryDisplayName
              + " directory '"
              + directory
              + "' does not exist, failing build (failOnUnknownFolder is true).";
      logger.error(message);
      throw new MojoFailureException(message);
    } else {
      logger.warn(
          directoryDisplayName + " directory '" + directory + "' does not exist, ignoring.");
    }
  }

  private void logNumberOfFilesFormatted() {
    logger.info("Successfully formatted " + filesFormatted.size() + " files.");
  }
}