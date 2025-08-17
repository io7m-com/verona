verona
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.verona/com.io7m.verona.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.verona%22)
[![Maven Central (snapshot)](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fcom%2Fio7m%2Fverona%2Fcom.io7m.verona%2Fmaven-metadata.xml&style=flat-square)](https://central.sonatype.com/repository/maven-snapshots/com/io7m/verona/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/verona.svg?style=flat-square)](https://codecov.io/gh/io7m-com/verona)
![Java Version](https://img.shields.io/badge/17-java?label=java&color=e65cc3)

![com.io7m.verona](./src/site/resources/verona.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/verona/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/verona/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/verona/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/verona/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/verona/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/verona/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/verona/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/verona/actions?query=workflow%3Amain.windows.temurin.lts)|

## verona

The `verona` package provides types implementing the
[Semantic Versioning](https://www.semver.org) specification.

## Features

* Types for representing versions and version ranges.
* Parsers for version numbers and version ranges.
* Written in pure Java 17.
* [OSGi](https://www.osgi.org/) ready.
* [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System) ready.
* ISC license.
* High-coverage automated test suite.

## Description

### Types

The `verona` package contains types for representing versions and version
ranges.

```
assert Version.of(1, 0, 2).toString() == "1.0.2";
assert Version.of(2, 3, 1, "SNAPSHOT").toString() == "2.3.1-SNAPSHOT";
assert Version.of(3, 0, 0, "SNAPSHOT").toString() == "2.3.1-SNAPSHOT";
```

Values of type `Version` are comparable and are ordered such that version
number components are treated as unsigned values and, for two versions `x`
and `y`:

```
Comparator.comparing(Version::major, COMPARE_UNSIGNED)
  .thenComparing(Version::minor, COMPARE_UNSIGNED)
  .thenComparing(Version::patch, COMPARE_UNSIGNED)
  .thenComparing(Version::qualifier, COMPARE_QUALIFIER)
  .compare(x, y);
```

For a given version `x.y.z`, the version `x.y.z-q` is considered less than
`x.y.z` for any `q`.

### Parsing

Version numbers can be parsed:

```
assert VersionParser.parse("1.0.2").toString() == "1.0.2";
```

Parsing is strict by default. This means that exactly three version number
components must be provided, along with an optional qualifier. Support is
provided for _lax_ parsing that allows for missing components. For example:

```
assert VersionParser.parse("1.2.0").toString() == "1.2.0";

VersionParser.parse("1.2"); // throws VersionException

assert VersionParser.parseLax("1.2").toString() == "1.2.0";
```

Additional support is provided for [OSGi](https://www.osgi.org) version
numbers where the qualifier is separated with a dot instead of a hyphen:

```
assert VersionParser.parseOSGi("1.2.0.SNAPSHOT").toString() == "1.2.0-SNAPSHOT";
```

