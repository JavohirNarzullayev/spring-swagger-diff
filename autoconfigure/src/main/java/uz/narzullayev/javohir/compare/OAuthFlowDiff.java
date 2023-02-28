package uz.narzullayev.javohir.compare;

import io.swagger.v3.oas.models.security.OAuthFlow;
import uz.narzullayev.javohir.model.ChangedOAuthFlow;
import uz.narzullayev.javohir.utils.ChangedUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static uz.narzullayev.javohir.utils.ChangedUtils.*;
import static uz.narzullayev.javohir.utils.ChangedUtils.isChanged;

public class OAuthFlowDiff {
  private final OpenApiDiff openApiDiff;

  public OAuthFlowDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  private static Map<String, Object> getExtensions(OAuthFlow oAuthFlow) {
    return ofNullable(oAuthFlow).map(OAuthFlow::getExtensions).orElse(null);
  }

  public Optional<ChangedOAuthFlow> diff(OAuthFlow left, OAuthFlow right) {
    ChangedOAuthFlow changedOAuthFlow = new ChangedOAuthFlow(left, right);
    if (left != null && right != null) {
      changedOAuthFlow
          .setAuthorizationUrl(
              !Objects.equals(left.getAuthorizationUrl(), right.getAuthorizationUrl()))
          .setTokenUrl(!Objects.equals(left.getTokenUrl(), right.getTokenUrl()))
          .setRefreshUrl(!Objects.equals(left.getRefreshUrl(), right.getRefreshUrl()));
    }
    openApiDiff
        .getExtensionsDiff()
        .diff(getExtensions(left), getExtensions(right))
        .ifPresent(changedOAuthFlow::setExtensions);
    return isChanged(changedOAuthFlow);
  }
}
