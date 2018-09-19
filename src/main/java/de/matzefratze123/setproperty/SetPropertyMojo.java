package de.matzefratze123.setproperty;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Mojo(name = "set-properties", defaultPhase = LifecyclePhase.VALIDATE)
public class SetPropertyMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private org.apache.maven.project.MavenProject project;

  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  protected MavenSession session;

  @Parameter
  private List<Operation> operations;

  @Parameter
  private List<Extraction> extractions;

  @Parameter(defaultValue = "${project.build.sourceEncoding}")
  protected String encoding;

  /**
   * The output directory into which to copy the resources.
   */
  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File outputDirectory;

  /**
   * The list of resources we want to transfer.
   */
  @Parameter
  private List<Resource> resources;

  /**
   * Expressions preceded with this string won't be interpolated. Anything else preceded with this string will be
   * passed through unchanged. For example {@code \${foo}} will be replaced with {@code ${foo}} but {@code \\${foo}}
   * will be replaced with {@code \\value of foo}, if this parameter has been set to the backslash.
   *
   * @since 2.3
   */
  @Parameter
  protected String escapeString;

  /**
   * Overwrite existing files even if the destination files are newer.
   *
   * @since 2.3
   */
  @Parameter(defaultValue = "false")
  private boolean overwrite;

  /**
   * Copy any empty directories included in the Resources.
   *
   * @since 2.3
   */
  @Parameter(defaultValue = "false")
  protected boolean includeEmptyDirs;

  @Parameter(defaultValue = "false")
  private boolean supportMultiLineFiltering;

  @Parameter(defaultValue = "true")
  protected boolean escapeWindowsPaths;

  @Parameter(defaultValue = "true")
  protected boolean filtering;

  @Component(role = MavenResourcesFiltering.class, hint = "default")
  protected MavenResourcesFiltering mavenResourcesFiltering;

  public void execute() throws MojoExecutionException, MojoFailureException {
    Properties properties = project.getProperties();

    if (operations != null) {
      for (Operation operation: operations) {
        List<SetProperty> propertiesSet = operation.execute(properties);

        for (SetProperty prop: propertiesSet) {
          getLog().info("Setting property '" + prop.getProperty() + "' to '" + prop.getValue() + "'...");
        }
      }
    }

    if (extractions != null) {
      for (Extraction extraction: extractions) {
        extraction.process(project, getLog());
      }
    }

    if (resources != null && !resources.isEmpty()) {
      try {
        filterFiles();
      } catch (MavenFilteringException e) {
        throw new MojoExecutionException("error filtering resources", e);
      }
    }
  }

  private void filterFiles() throws MavenFilteringException {
    MavenResourcesExecution mavenResourcesExecution =
        new MavenResourcesExecution(resources, outputDirectory, project, encoding, Collections.<String>emptyList(),
                                    Collections.<String>emptyList(), session);

    for (Resource resource: resources) {
      if (resource.getFiltering() == null || resource.getFiltering().trim().isEmpty()) {
        resource.setFiltering(filtering);
      }
    }
    mavenResourcesExecution.setInjectProjectBuildFilters(true);
    mavenResourcesExecution.setEscapeWindowsPaths(escapeWindowsPaths);
    mavenResourcesExecution.setEscapeString(escapeString);
    mavenResourcesExecution.setOverwrite(overwrite);
    mavenResourcesExecution.setIncludeEmptyDirs(includeEmptyDirs);
    mavenResourcesExecution.setSupportMultiLineFiltering(supportMultiLineFiltering);
    mavenResourcesExecution.setDelimiters(null);
/*
    mavenResourcesExecution.setFilterFilenames( fileNameFiltering );
    mavenResourcesExecution.setAddDefaultExcludes( addDefaultExcludes );
    mavenResourcesExecution.setAdditionalProperties( additionalProperties );
*/
    mavenResourcesFiltering.filterResources(mavenResourcesExecution);
  }


}
