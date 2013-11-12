package org.apache.olingo.producer.core.uri;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmBindingTarget;

public class UriInfo {
  private UriType uriType;
  private EdmBindingTarget bindingTarget;
  private List<String> keyNames = Collections.emptyList();

  public UriType getUriType() {
    return uriType;
  }

  public void setUriType(final UriType uriType) {
    this.uriType = uriType;
  }

  public EdmBindingTarget getBindingTarget() {
    return bindingTarget;
  }

  public void setBindingTarget(final EdmBindingTarget bindingTarget) {
    this.bindingTarget = bindingTarget;
  }

  public void setKeyNames(final List<String> keyNames) {
    this.keyNames = keyNames;
  }

  public List<String> getKeyNames() {
    return keyNames;
  }
}
