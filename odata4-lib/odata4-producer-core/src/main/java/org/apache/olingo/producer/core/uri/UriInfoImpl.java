package org.apache.olingo.producer.core.uri;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;

public class UriInfoImpl extends UriInfo {
  private Edm edm = null;
  private List<UriPathInfoImpl> pathInfos = new ArrayList<UriPathInfoImpl>();

  public Edm getEdm() {
    return edm;
  }

  public void addUriPathInfo(final UriPathInfoImpl uriPathInfoImpl) {
    pathInfos.add(uriPathInfoImpl);
  }

  public UriPathInfoImpl getLastUriPathInfo() {
    if (!pathInfos.isEmpty()) {
      return pathInfos.get(pathInfos.size() - 1);
    } else {
      return null;
    }
  }

}
