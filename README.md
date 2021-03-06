# Seismic

![seismic](https://skhnv.com/wp-content/uploads/2020/01/Снимок-экрана-2020-01-24-в-15.12.03.png)
A Java library for work with seismic data. Using this library you can get general information about SGY/SEGY file.

```java
Seismic seismic = new Seismic();
```

Get text header:
```java
seismic.getTextHeader();
```

Get binary header:
```java
seismic.getBinaryHeader();
```

Also, get description about binary header from SEG-Y rev.1:
```java
seismic.getDescriptionByElementInBinaryHeader();
```

Get every trace header:
```java
seismic.getTraceHeader(long traceNumber);
```

And description for header in very trace:
```java
seismic.getDescriptionByElementInTraceHeader(long traceNumber);
```

Get every trace data:
```java
seismic.getTraceData(long traceNumber);
```

Get samples in every trace:
```java
seismic.getSamplesInEveryTrace();
```

Also, you can get multiple data. For getting all traces in one array:

```java
seismic.getAllTraceData();
```

For getting all trace headers in one array:

```java
seismic.getAllTraceHeaders();
```

For getting all X and Y coordinates (it works if X & Y coordinated located in 73, 77 bytes only):

```java
seismic.getAllXYForEveryTrace();
``` 