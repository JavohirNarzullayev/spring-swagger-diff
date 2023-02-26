package uz.narzullayev.javohir;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import uz.narzullayev.javohir.output.HtmlRender;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A Maven Mojo that diffs two OpenAPI specifications and reports on differences.
 */
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

    @Parameter(property = "htmlPage")
    String htmlPage;


    @Component
    protected MavenProject project;

    @Parameter(defaultValue = "${project.basedir}/src/main/resources", required = true, readonly = true)
    private String sourceResourceDir;

    @Parameter(defaultValue = "${project.basedir}/target", required = true, readonly = true)
    private String targetResourceDir;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    protected ArtifactRepository localRepository;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final var diff = OpenApiCompare.fromLocations(oldSpec, newSpec);
            var html = new HtmlRender(
                    "Swagger Diff",
                    "http://deepoove.com/swagger-diff/stylesheets/demo.css"
            ).render(diff);
            try {
                String fileName = targetResourceDir + "/changelog.html";
                htmlPage = fileName;
                var fw = new FileWriter(fileName);
                fw.write(html);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (RuntimeException e) {
            throw new MojoExecutionException("Unexpected error", e);
        }
    }
}
