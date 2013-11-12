package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;

public class ActionMapKey {
  private final FullQualifiedName actionName;
  private final FullQualifiedName bindingParameterTypeName;
  private final Boolean isBindingParameterCollection;

  public ActionMapKey(final FullQualifiedName actionName, final FullQualifiedName bindingParameterTypeName,
      final Boolean isBindingParameterCollection) {
    this.actionName = actionName;
    this.bindingParameterTypeName = bindingParameterTypeName;
    this.isBindingParameterCollection = isBindingParameterCollection;
  }

  @Override
  public int hashCode() {
    String forHash = actionName.toString();

    if (bindingParameterTypeName != null) {
      forHash = forHash + bindingParameterTypeName.toString();
    } else {
      forHash = forHash + "TypeNull";
    }

    if (isBindingParameterCollection != null) {
      forHash = forHash + isBindingParameterCollection.toString();
    } else {
      forHash = forHash + "CollectionNull";
    }

    return forHash.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof ActionMapKey)) {
      return false;
    }
    final ActionMapKey other = (ActionMapKey) obj;

    if (actionName.equals(other.actionName)) {
      if ((bindingParameterTypeName == null && other.bindingParameterTypeName == null)
          || (bindingParameterTypeName != null && bindingParameterTypeName.equals(other.bindingParameterTypeName))) {
        if ((isBindingParameterCollection == null && other.isBindingParameterCollection == null)
            || (isBindingParameterCollection != null && isBindingParameterCollection
                .equals(other.isBindingParameterCollection))) {
          return true;
        }
      }
    }
    return false;
  }
}
