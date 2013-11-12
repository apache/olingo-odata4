package org.apache.olingo.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.junit.Test;

public class EdmTypeImplTest {

  @Test
  public void getterTest() {
    EdmType type = new EdmTypeImplTester(new FullQualifiedName("namespace", "name"), EdmTypeKind.UNDEFINED);
    assertEquals("name", type.getName());
    assertEquals("namespace", type.getNamespace());
    assertEquals(EdmTypeKind.UNDEFINED, type.getKind());
  }

  private class EdmTypeImplTester extends EdmTypeImpl {
    public EdmTypeImplTester(final FullQualifiedName name, final EdmTypeKind kind) {
      super(name, kind);
    }
  }

}
