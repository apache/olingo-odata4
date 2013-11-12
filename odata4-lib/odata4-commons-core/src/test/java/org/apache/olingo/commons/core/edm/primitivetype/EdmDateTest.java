package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmDateTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeKind.Date.getEdmPrimitiveTypeInstance();

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("2009-12-26", instance.toUriLiteral("2009-12-26"));
    assertEquals("-2009-12-26", instance.toUriLiteral("-2009-12-26"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("2009-12-26", instance.fromUriLiteral("2009-12-26"));
    assertEquals("-2009-12-26", instance.fromUriLiteral("-2009-12-26"));
  }

  @Test
  public void valueToString() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    dateTime.setTimeZone(TimeZone.getTimeZone("GMT-11:30"));
    dateTime.set(2012, 1, 29, 13, 0, 0);
    assertEquals("2012-02-29", instance.valueToString(dateTime, null, null, null, null, null));

    final Long millis = 1330558323007L;
    assertEquals("2012-02-29", instance.valueToString(millis, null, null, null, null, null));

    assertEquals("1969-12-31", instance.valueToString(new Date(-43200000), null, null, null, null, null));

    dateTime.set(Calendar.YEAR, 12344);
    assertEquals("12344-02-29", instance.valueToString(dateTime, null, null, null, null, null));

    expectTypeErrorInValueToString(instance, 0);
  }

  @Test
  public void valueOfString() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    dateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    dateTime.set(2012, 1, 29);
    assertEquals(dateTime, instance.valueOfString("2012-02-29", null, null, null, null, null, Calendar.class));
    assertEquals(Long.valueOf(dateTime.getTimeInMillis()), instance.valueOfString("2012-02-29", null, null, null, null,
        null, Long.class));
    assertEquals(dateTime.getTime(), instance.valueOfString("2012-02-29", null, null, null, null, null, Date.class));

    dateTime.set(Calendar.YEAR, 12344);
    assertEquals(dateTime, instance.valueOfString("12344-02-29", null, null, null, null, null, Calendar.class));

    // TODO: Clarify whether negative years are really needed.
    // dateTime.set(-1, 1, 28);
    // assertEquals(dateTime, instance.valueOfString("-0001-02-28", null, Calendar.class));

    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02");
    expectContentErrorInValueOfString(instance, "2012-02-30");
    expectContentErrorInValueOfString(instance, "20120229");
    expectContentErrorInValueOfString(instance, "2012-02-1");
    expectContentErrorInValueOfString(instance, "2012-2-12");
    expectContentErrorInValueOfString(instance, "123-02-03");

    expectTypeErrorInValueOfString(instance, "2012-02-29");
  }
}
