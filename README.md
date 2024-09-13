# CXF issue
This repo is aimed to reproduce an issue while using CXF to call a SOAP endpoint. 
The issue is that if the endpoint is not reachable, the client will loop forever trying to connect to it.

# How to reproduce the issue
Just run:

    ./mvnw test 

You will see an error like:

    java.net.http.HttpConnectTimeoutException: HttpConnectTimeoutException invoking http://www.google.com:88/calculator.asmx?WSDL: HTTP connect timed out
        at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:77)
        at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        at java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)
        at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:480)
        at org.apache.cxf.transport.http.HTTPConduit$WrappedOutputStream.mapException(HTTPConduit.java:1452)
        at org.apache.cxf.transport.http.HTTPConduit$WrappedOutputStream.close(HTTPConduit.java:1433)
        at org.apache.cxf.transport.AbstractConduit.close(AbstractConduit.java:56)
        at org.apache.cxf.transport.http.HTTPConduit.close(HTTPConduit.java:717)

and the test will loop on that error forever.

# Expected behavior
The test should fail with a timeout exception after a while, but it should not loop forever.
