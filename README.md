# Seismic

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


Usually seismic data were saved in IBM 32bit floating point format. Here you can 