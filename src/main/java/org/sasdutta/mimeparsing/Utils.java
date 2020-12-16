package org.sasdutta.mimeparsing;

import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

public class Utils {
  public static JsonValue toJsonValue(String value) {
    if (value == null) return JsonValue.NULL;
    else return JsonProvider.provider().createValue(value);
  }
}
