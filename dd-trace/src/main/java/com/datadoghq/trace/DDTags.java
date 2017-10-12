package com.datadoghq.trace;

public class DDTags {
  public static final String SPAN_TYPE = "span.type";
  public static final String SERVICE_NAME = "service.name";
  public static final String RESOURCE_NAME = "resource.name";
  public static final String THREAD_NAME = "thread.name";
  public static final String THREAD_ID = "thread.id";
  public static final String DB_STATEMENT = "sql.query";

  public static final String ERROR_MSG = "error.msg"; // a string representing the error message
  public static final String ERROR_TYPE =
      "error.type"; // a string representing the type of the error
  public static final String ERROR_STACK =
      "error.stack"; // a human readable version of the stack. beta.
}
