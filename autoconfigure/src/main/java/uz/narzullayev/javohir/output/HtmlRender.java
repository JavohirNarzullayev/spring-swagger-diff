package uz.narzullayev.javohir.output;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.specialized.*;

import uz.narzullayev.javohir.model.*;
import uz.narzullayev.javohir.utils.RefPointer;
import uz.narzullayev.javohir.utils.RefType;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static j2html.TagCreator.*;
import static uz.narzullayev.javohir.model.Changed.result;

public class HtmlRender implements Render {

  private static final RefPointer<Schema<?>> refPointer = new RefPointer<>(RefType.SCHEMAS);
  public static final String COMMENT = "comment";
  public static final String MISSING = "missing";

  private final String title;
  private final String linkCss;
  protected ChangedOpenApi diff;

  public HtmlRender() {
    this("Api Change Log", "http://deepoove.com/swagger-diff/stylesheets/demo.css");
  }

  public HtmlRender(String title, String linkCss) {
    this.title = title;
    this.linkCss = linkCss;
  }

  public String render(ChangedOpenApi diff) {
    this.diff = diff;

    List<Endpoint> newEndpoints = diff.getNewEndpoints();
    OlTag ol_newEndpoint = ol_newEndpoint(newEndpoints);

    List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
    OlTag ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

    List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
    OlTag ol_deprecatedEndpoint = ol_deprecatedEndpoint(deprecatedEndpoints);

    List<ChangedOperation> changedOperations = diff.getChangedOperations();
    OlTag ol_changed = ol_changed(changedOperations);

    return renderHtml(ol_newEndpoint, ol_missingEndpoint, ol_deprecatedEndpoint, ol_changed);
  }

  public String renderHtml(OlTag ol_new, OlTag ol_miss, OlTag ol_deprec, OlTag ol_changed) {
    HtmlTag html =
        TagCreator.html()
            .attr("lang", "en")
            .with(
                TagCreator.head()
                    .with(
                        TagCreator.meta().withCharset("utf-8"),
                        TagCreator.title(title),
                        TagCreator.link().withRel("stylesheet").withHref(linkCss)),
                TagCreator.body()
                    .with(
                        TagCreator.header().with(TagCreator.h1(title)),
                        TagCreator.div()
                            .withClass("article")
                            .with(
                                TagCreator.div().with(TagCreator.h2("What's New"), TagCreator.hr(), ol_new),
                                TagCreator.div().with(TagCreator.h2("What's Deleted"), TagCreator.hr(), ol_miss),
                                TagCreator.div().with(TagCreator.h2("What's Deprecated"), TagCreator.hr(), ol_deprec),
                                TagCreator.div().with(TagCreator.h2("What's Changed"), TagCreator.hr(), ol_changed))));

    return TagCreator.document().render() + html.render();
  }

  private OlTag ol_newEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return TagCreator.ol();
    OlTag ol = TagCreator.ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_newEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private LiTag li_newEndpoint(String method, String path, String desc) {
    return TagCreator.li().with(TagCreator.span(method).withClass(method)).withText(path + " ").with(TagCreator.span(desc));
  }

  private OlTag ol_missingEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return TagCreator.ol();
    OlTag ol = TagCreator.ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_missingEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private LiTag li_missingEndpoint(String method, String path, String desc) {
    return TagCreator.li().with(TagCreator.span(method).withClass(method), TagCreator.del().withText(path)).with(TagCreator.span(" " + desc));
  }

  private OlTag ol_deprecatedEndpoint(List<Endpoint> endpoints) {
    if (null == endpoints) return TagCreator.ol();
    OlTag ol = TagCreator.ol();
    for (Endpoint endpoint : endpoints) {
      ol.with(
          li_deprecatedEndpoint(
              endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
    }
    return ol;
  }

  private LiTag li_deprecatedEndpoint(String method, String path, String desc) {
    return TagCreator.li().with(TagCreator.span(method).withClass(method), TagCreator.del().withText(path)).with(TagCreator.span(" " + desc));
  }

  private OlTag ol_changed(List<ChangedOperation> changedOperations) {
    if (null == changedOperations) return TagCreator.ol();
    OlTag ol = TagCreator.ol();
    for (ChangedOperation changedOperation : changedOperations) {
      String pathUrl = changedOperation.getPathUrl();
      String method = changedOperation.getHttpMethod().toString();
      String desc =
          Optional.ofNullable(changedOperation.getSummary())
              .map(ChangedMetadata::getRight)
              .orElse("");

      UlTag ul_detail = TagCreator.ul().withClass("detail");
      if (result(changedOperation.getParameters()).isDifferent()) {
        ul_detail.with(
            TagCreator.li().with(TagCreator.h3("Parameters")).with(ul_param(changedOperation.getParameters())));
      }
      if (changedOperation.resultRequestBody().isDifferent()) {
        ul_detail.with(
            TagCreator.li().with(TagCreator.h3("Request"))
                .with(ul_request(changedOperation.getRequestBody().getContent())));
      }
      if (changedOperation.resultApiResponses().isDifferent()) {
        ul_detail.with(
            TagCreator.li().with(TagCreator.h3("Response")).with(ul_response(changedOperation.getApiResponses())));
      }
      ol.with(
          TagCreator.li().with(TagCreator.span(method).withClass(method))
              .withText(pathUrl + " ")
              .with(TagCreator.span(desc))
              .with(ul_detail));
    }
    return ol;
  }

  private UlTag ul_response(ChangedApiResponse changedApiResponse) {
    Map<String, ApiResponse> addResponses = changedApiResponse.getIncreased();
    Map<String, ApiResponse> delResponses = changedApiResponse.getMissing();
    Map<String, ChangedResponse> changedResponses = changedApiResponse.getChanged();
    UlTag ul = TagCreator.ul().withClass("change response");
    for (Entry<String, ApiResponse> prop : addResponses.entrySet()) {
      ul.with(li_addResponse(prop.getKey(), prop.getValue()));
    }
    for (Entry<String, ApiResponse> prop : delResponses.entrySet()) {
      ul.with(li_missingResponse(prop.getKey(), prop.getValue()));
    }
    for (Entry<String, ChangedResponse> prop : changedResponses.entrySet()) {
      ul.with(li_changedResponse(prop.getKey(), prop.getValue()));
    }
    return ul;
  }

  private LiTag li_addResponse(String name, ApiResponse response) {
    return TagCreator.li().withText(String.format("New response : [%s]", name))
        .with(
            TagCreator.span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_missingResponse(String name, ApiResponse response) {
    return TagCreator.li().withText(String.format("Deleted response : [%s]", name))
        .with(
            TagCreator.span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_changedResponse(String name, ChangedResponse response) {
    return TagCreator.li().withText(String.format("Changed response : [%s]", name))
        .with(
            span((null == response.getNewApiResponse()
                        || null == response.getNewApiResponse().getDescription())
                    ? ""
                    : ("//" + response.getNewApiResponse().getDescription()))
                .withClass(COMMENT))
        .with(ul_request(response.getContent()));
  }

  private UlTag ul_request(ChangedContent changedContent) {
    UlTag ul = TagCreator.ul().withClass("change request-body");
    if (changedContent != null) {
      for (String propName : changedContent.getIncreased().keySet()) {
        ul.with(li_addRequest(propName, changedContent.getIncreased().get(propName)));
      }
      for (String propName : changedContent.getMissing().keySet()) {
        ul.with(li_missingRequest(propName, changedContent.getMissing().get(propName)));
      }
      for (String propName : changedContent.getChanged().keySet()) {
        ul.with(li_changedRequest(propName, changedContent.getChanged().get(propName)));
      }
    }
    return ul;
  }

  private LiTag li_addRequest(String name, MediaType request) {
    return TagCreator.li().withText(String.format("New body: '%s'", name));
  }

  private LiTag li_missingRequest(String name, MediaType request) {
    return TagCreator.li().withText(String.format("Deleted body: '%s'", name));
  }

  private LiTag li_changedRequest(String name, ChangedMediaType request) {
    LiTag li =
        TagCreator.li().with(div_changedSchema(request.getSchema()))
            .withText(String.format("Changed body: '%s'", name));
    if (request.isIncompatible()) {
      incompatibilities(li, request.getSchema());
    }
    return li;
  }

  private DivTag div_changedSchema(ChangedSchema schema) {
    DivTag div = TagCreator.div();
    div.with(TagCreator.h3("Schema" + (schema.isIncompatible() ? " incompatible" : "")));
    return div;
  }

  private void incompatibilities(final LiTag output, final ChangedSchema schema) {
    incompatibilities(output, "", schema);
  }

  private void incompatibilities(
      final ContainerTag<?> output, String propName, final ChangedSchema schema) {
    if (schema.getItems() != null) {
      items(output, propName, schema.getItems());
    }
    if (schema.isCoreChanged() == DiffResult.INCOMPATIBLE && schema.isChangedType()) {
      String type = type(schema.getOldSchema()) + " -> " + type(schema.getNewSchema());
      property(output, propName, "Changed property type", type);
    }
    String prefix = propName.isEmpty() ? "" : propName + ".";
    properties(
        output, prefix, "Missing property", schema.getMissingProperties(), schema.getContext());
    schema
        .getChangedProperties()
        .forEach((name, property) -> incompatibilities(output, prefix + name, property));
  }

  private void items(ContainerTag<?> output, String propName, ChangedSchema schema) {
    incompatibilities(output, propName + "[n]", schema);
  }

  private void properties(
      ContainerTag<?> output,
      String propPrefix,
      String title,
      Map<String, Schema<?>> properties,
      DiffContext context) {
    if (properties != null) {
      properties.forEach((key, value) -> resolveProperty(output, propPrefix, key, value, title));
    }
  }

  private void resolveProperty(
      ContainerTag<?> output, String propPrefix, String key, Schema<?> value, String title) {
    try {
      property(output, propPrefix + key, title, resolve(value));
    } catch (Exception e) {
      property(output, propPrefix + key, title, type(value));
    }
  }

  protected void property(ContainerTag<?> output, String name, String title, Schema<?> schema) {
    property(output, name, title, type(schema));
  }

  protected void property(ContainerTag<?> output, String name, String title, String type) {
    output.with(TagCreator.p(String.format("%s: %s (%s)", title, name, type)).withClass(MISSING));
  }

  protected Schema<?> resolve(Schema<?> schema) {
    return refPointer.resolveRef(
        diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
  }

  protected String type(Schema<?> schema) {
    String result = "object";
    if (schema == null) {
      result = "no schema";
    } else if (schema instanceof ArraySchema) {
      result = "array";
    } else if (schema.getType() != null) {
      result = schema.getType();
    }
    return result;
  }

  private UlTag ul_param(ChangedParameters changedParameters) {
    List<Parameter> addParameters = changedParameters.getIncreased();
    List<Parameter> delParameters = changedParameters.getMissing();
    List<ChangedParameter> changed = changedParameters.getChanged();
    UlTag ul = TagCreator.ul().withClass("change param");
    for (Parameter param : addParameters) {
      ul.with(li_addParam(param));
    }
    for (ChangedParameter param : changed) {
      ul.with(li_changedParam(param));
    }
    for (Parameter param : delParameters) {
      ul.with(li_missingParam(param));
    }
    return ul;
  }

  private LiTag li_addParam(Parameter param) {
    return TagCreator.li().withText("Add " + param.getName() + " in " + param.getIn())
        .with(
            TagCreator.span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_missingParam(Parameter param) {
    return TagCreator.li().withClass(MISSING)
        .with(TagCreator.span("Delete"))
        .with(TagCreator.del(param.getName()))
        .with(TagCreator.span("in ").withText(param.getIn()))
        .with(
            TagCreator.span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_deprecatedParam(ChangedParameter param) {
    return TagCreator.li().withClass(MISSING)
        .with(TagCreator.span("Deprecated"))
        .with(del(param.getName()))
        .with(TagCreator.span("in ").withText(param.getIn()))
        .with(
            span(null == param.getNewParameter().getDescription()
                    ? ""
                    : ("//" + param.getNewParameter().getDescription()))
                .withClass(COMMENT));
  }

  private LiTag li_changedParam(ChangedParameter changeParam) {
    if (changeParam.isDeprecated()) {
      return li_deprecatedParam(changeParam);
    }
    boolean changeRequired = changeParam.isChangeRequired();
    boolean changeDescription =
        Optional.ofNullable(changeParam.getDescription())
            .map(ChangedMetadata::isDifferent)
            .orElse(false);
    Parameter rightParam = changeParam.getNewParameter();
    Parameter leftParam = changeParam.getNewParameter();
    LiTag li = TagCreator.li().withText(changeParam.getName() + " in " + changeParam.getIn());
    if (changeRequired) {
      li.withText(" change into " + (rightParam.getRequired() ? "required" : "not required"));
    }
    if (changeDescription) {
      li.withText(" Notes ")
          .with(TagCreator.del(leftParam.getDescription()).withClass(COMMENT))
          .withText(" change into ")
          .with(TagCreator.span(rightParam.getDescription()).withClass(COMMENT));
    }
    return li;
  }
}
