package uz.narzullayev.javohir;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import uz.narzullayev.javohir.model.ChangedOpenApi;
import uz.narzullayev.javohir.output.ConsoleRender;
import uz.narzullayev.javohir.output.MarkdownRender;

/** A Maven Mojo that diffs two OpenAPI specifications and reports on differences. */
@Mojo(name = "diff", defaultPhase = LifecyclePhase.TEST)
public class OpenApiDiffMojo extends AbstractMojo {
  @Parameter(property = "oldSpec")
  String oldSpec;

  @Parameter(property = "newSpec")
  String newSpec;

  @Parameter(property = "failOnIncompatible", defaultValue = "false")
  Boolean failOnIncompatible = false;

  @Parameter(property = "failOnChanged", defaultValue = "false")
  Boolean failOnChanged = false;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
       String OPENAPI_DOC1="http://localhost:8080/v1/test/api-docs";
       String OPENAPI_DOC2="https://mobile-id-api.licenses.uz/v1/swagger/api-docs/application";

      final ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
      getLog().error("Error start");
      getLog().info(new MarkdownRender().render(diff));

    } catch (RuntimeException e) {
      throw new MojoExecutionException("Unexpected error", e);
    }
  }
}
