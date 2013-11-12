package org.apache.olingo.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;

import org.apache.olingo.commons.api.edm.EdmNamed;
import org.junit.Test;

public class EdmNamedImplTest {

  @Test
  public void getNameTest() {
    EdmNamed obj = new EdmNamedImplTester("Name");
    assertEquals("Name", obj.getName());
  }

  private class EdmNamedImplTester extends EdmNamedImpl {

    public EdmNamedImplTester(final String name) {
      super(name);
    }
  }

}
