package org.apache.olingo.commons.core.edm.provider;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.commons.api.edm.helper.EdmEntitySetInfo;
import org.apache.olingo.commons.api.edm.helper.EdmFunctionImportInfo;
import org.apache.olingo.commons.api.edm.helper.EdmSingletonInfo;

//TODO: Test
public class EdmServiceMetadataImpl implements EdmServiceMetadata {

  @Override
  public InputStream getMetadata() {
    return null;
  }

  @Override
  public String getDataServiceVersion() {
    return null;
  }

  @Override
  public List<EdmEntitySetInfo> getEntitySetInfos() {
    return null;
  }

  @Override
  public List<EdmSingletonInfo> getSingletonInfos() {
    return null;
  }

  @Override
  public List<EdmFunctionImportInfo> getFunctionImportInfos() {
    return null;
  }

}
