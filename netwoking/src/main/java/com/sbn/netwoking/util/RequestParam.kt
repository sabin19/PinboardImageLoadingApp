package com.sbn.netwoking.util


class RequestParam<out A, out B>(
    public val key: A,
    public val value: B
)

public class StringRequestParam<out A, out B, out C>(
    public val url: String,
    public val method: A,
    public val parameters: B?,
    public val headers: C?
) where  A : Method, B : List<RequestParam<String, String>>?, C : List<RequestParam<String, String>>?