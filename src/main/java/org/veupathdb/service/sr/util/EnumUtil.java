package org.veupathdb.service.sr.util;

import jakarta.ws.rs.NotFoundException;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumUtil {

  private EnumUtil(){}

  public static <E extends Enum<E>> String getSupportedValuesString(E[] values) {
    return Arrays
        .stream(values)
        .map(Enum::name)
        .map(String::toLowerCase)
        .collect(Collectors.joining(", "));
  }

  public static <E extends Enum<E>, X extends Exception> E validate(String str, E[] values, Function<String, X> exceptionProvider) throws X {
    return Arrays
        .stream(values)
        .filter(val -> val.name().equalsIgnoreCase(str))
        .findFirst()
        .orElseThrow(() -> exceptionProvider.apply(
            "Unsupported value '" + str + "'.  Only [ " +
                 getSupportedValuesString(values) + " ] supported."));
  }

}
