# klogging
Functional Logging Library based on [SL4J](http://www.slf4j.org/)  and [Logback](http://logback.qos.ch/)  

[![](https://jitpack.io/v/sahlone/klogging.svg?label=Release)](https://jitpack.io/#sahlone/klogging)

klogging is a functional approach to dealing with logging in json format with support for markers
### Installation
The library can be obtained from [Jitpack](https://jitpack.io/#sahlone/klogging).
```Gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.sahlone:klogging:$version'
}
```
### Usage
Create Logger object
```
val logger = createContextualLogger()
```
Log statement examples. Please look into test cases for more examples.
```
 logger.debug(TracingContext(), {"data" to DataObject()}) { "A debug message" }
```

```
 logger.errorWithCause(TracingContext(),throwable, {"data" to DataObject()}) { "A error message" }
```


###  Reporting issues
Don't shy away from creating  a github issue, A  PR for the fix is always  welcome as well.
