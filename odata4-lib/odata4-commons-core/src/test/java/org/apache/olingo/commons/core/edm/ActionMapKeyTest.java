package org.apache.olingo.commons.core.edm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.junit.Test;

public class ActionMapKeyTest {

  private final FullQualifiedName fqn = new FullQualifiedName("namespace", "name");
  private final FullQualifiedName fqnType = new FullQualifiedName("namespace2", "name2");

  @Test
  public void testEqualsMethod() {
    ActionMapKey key1 = new ActionMapKey(fqn, null, null);
    ActionMapKey someKey = new ActionMapKey(fqn, null, null);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, null, new Boolean(true));
    someKey = new ActionMapKey(fqn, null, true);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, false);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, null);
    someKey = new ActionMapKey(fqn, fqnType, null);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, null);
    assertNotSame(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key1, someKey);

    key1 = new ActionMapKey(fqn, null, true);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key1, someKey);

    key1 = new ActionMapKey(fqn, null, true);
    someKey = new ActionMapKey(fqn, null, false);
    assertNotSame(key1, someKey);
  }

  @Test
  public void testHashMethod() {
    ActionMapKey key1 = new ActionMapKey(fqn, null, null);
    ActionMapKey someKey = new ActionMapKey(fqn, null, null);
    assertEquals(key1.hashCode(), someKey.hashCode());

    key1 = new ActionMapKey(fqn, null, new Boolean(true));
    someKey = new ActionMapKey(fqn, null, true);
    assertEquals(key1.hashCode(), someKey.hashCode());

    someKey = new ActionMapKey(fqn, fqnType, true);
    assertNotSame(key1.hashCode(), someKey.hashCode());

    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key1.hashCode(), someKey.hashCode());
  }

}
