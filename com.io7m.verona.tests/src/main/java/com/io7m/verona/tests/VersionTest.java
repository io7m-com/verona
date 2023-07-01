/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.verona.tests;

import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionException;
import com.io7m.verona.core.VersionParser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class VersionTest
{
  /**
   * @return A set of invalid version tests
   */

  @TestFactory
  public Stream<DynamicTest> testInvalidVersions()
  {
    return Stream.of(
      "",
      "1",
      "1.0",
      "1.a",
      "1.0.0-",
      "4294967296.0.0",
      "0.4294967296.0",
      "0.0.4294967296",
      "1.0.0-β",
      "v1.2.3"
    ).map(VersionTest::invalidVersionTestOf);
  }

  /**
   * @return A set of valid version tests
   */

  @TestFactory
  public Stream<DynamicTest> testValidVersions()
  {
    return Stream.of(
      "1.0.0",
      "1.0.0-SNAPSHOT",
      "1.0.0-alpha",
      "1.0.0-alpha.1",
      "1.0.0-alpha.beta",
      "1.0.0-beta",
      "1.0.0-beta.2",
      "1.0.0-beta.11",
      "1.0.0-rc.1",
      "1.0.0",
      "1.0.0-0.3.7",
      "1.0.0-x.7.z.92",
      "1.0.0-x-y-z.-"
    ).map(VersionTest::validVersionTestOf);
  }

  /**
   * An invalid version.
   */

  @Test
  public void testInvalidQualifier0()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      Version.of(1, 0, 0, "β");
    });
  }

  /**
   * Version ordering.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOrdering0()
    throws Exception
  {
    {
      final var v0 = VersionParser.parse("1.0.0");
      final var v1 = VersionParser.parse("1.0.0");
      assertEquals(0, v0.compareTo(v1));
      assertFalse(v0.isSnapshot());
      assertFalse(v1.isSnapshot());
    }

    {
      final var v0 = VersionParser.parse("2.0.0");
      final var v1 = VersionParser.parse("1.0.0");
      assertTrue(v0.compareTo(v1) > 0);
      assertTrue(v1.compareTo(v0) < 0);
      assertFalse(v0.isSnapshot());
      assertFalse(v1.isSnapshot());
    }

    {
      final var v0 = VersionParser.parse("1.2.0");
      final var v1 = VersionParser.parse("1.0.0");
      assertTrue(v0.compareTo(v1) > 0);
      assertTrue(v1.compareTo(v0) < 0);
      assertFalse(v0.isSnapshot());
      assertFalse(v1.isSnapshot());
    }

    {
      final var v0 = VersionParser.parse("1.0.2");
      final var v1 = VersionParser.parse("1.0.0");
      assertTrue(v0.compareTo(v1) > 0);
      assertTrue(v1.compareTo(v0) < 0);
      assertFalse(v0.isSnapshot());
      assertFalse(v1.isSnapshot());
    }

    {
      final var v0 = VersionParser.parse("1.0.0");
      final var v1 = VersionParser.parse("1.0.0-SNAPSHOT");
      assertTrue(v0.compareTo(v1) > 0);
      assertTrue(v1.compareTo(v0) < 0);
      assertFalse(v0.isSnapshot());
      assertTrue(v1.isSnapshot());
    }

    {
      final var v0 = VersionParser.parse("1.0.0-SNAPSHOT");
      final var v1 = VersionParser.parse("1.0.0-SNAPSHOT");
      assertEquals(0, v0.compareTo(v1));
      assertTrue(v0.isSnapshot());
      assertTrue(v1.isSnapshot());
    }

    {
      final var v0 = VersionParser.parse("1.0.0-B");
      final var v1 = VersionParser.parse("1.0.0-A");
      assertTrue(v0.compareTo(v1) > 0);
      assertTrue(v1.compareTo(v0) < 0);
      assertFalse(v0.isSnapshot());
      assertFalse(v1.isSnapshot());
    }
  }

  private static DynamicTest invalidVersionTestOf(
    final String text)
  {
    return DynamicTest.dynamicTest(
      "testInvalidVersion_%s".formatted(text),
      () -> {
        final var ex =
          assertThrows(VersionException.class, () -> {
            VersionParser.parse(text);
          });
        ex.printStackTrace(System.err);
      });
  }

  private static DynamicTest validVersionTestOf(
    final String text)
  {
    return DynamicTest.dynamicTest(
      "testValidVersion_%s".formatted(text),
      () -> {
        final var p0 = VersionParser.parse(text);
        final var p1 = VersionParser.parse(text);
        assertEquals(p0, p1);
        assertEquals(p0, p0);
        assertEquals(p0.hashCode(), p1.hashCode());
        assertEquals(p0.toString(), p1.toString());
        assertEquals(text, p0.toString());
        assertEquals(0, p0.compareTo(p1));
        assertNotEquals(p0, Integer.valueOf(23));
      });
  }

  /**
   * Ensure that the ordering relation matches the semver spec.
   */

  @Test
  public void testSpecOrdering0()
  {
    final var names =
      List.of(
        "1.0.0-SNAPSHOT",
        "1.0.0-alpha",
        "1.0.0-alpha.1",
        "1.0.0-alpha.beta",
        "1.0.0-beta",
        "1.0.0-beta.2",
        "1.0.0-beta.11",
        "1.0.0-rc.1",
        "1.0.0"
      );

    final var versions =
      names.stream()
        .map(x -> {
          try {
            return VersionParser.parse(x);
          } catch (final VersionException e) {
            throw new RuntimeException(e);
          }
        }).toList();

    final var versionsSorted =
      versions.stream()
        .sorted()
        .toList();

    assertEquals(versions, versionsSorted);
  }

  /**
   * Ensure that the ordering relation matches the semver spec.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSpecOrdering1()
    throws Exception
  {
    final var v = VersionParser.parse("1.0.0-SNAPSHOT");
    final var q = v.qualifier().get();
    assertEquals(0, q.compareTo(q));
  }

  /**
   * @return A set of invalid version tests
   */

  @TestFactory
  public Stream<DynamicTest> testInvalidOSGiVersions()
  {
    return Stream.of(
      "",
      "1",
      "1.0",
      "1.a",
      "1.0.0-",
      "4294967296.0.0",
      "0.4294967296.0",
      "0.0.4294967296",
      "1.0.0-β",
      "v1.2.3",
      "1.0.0-SNAPSHOT",
      "1.0.0-alpha",
      "1.0.0-alpha.1",
      "1.0.0-alpha.beta",
      "1.0.0-beta",
      "1.0.0-beta.2",
      "1.0.0-beta.11",
      "1.0.0-rc.1",
      "1.0.0-0.3.7",
      "1.0.0-x.7.z.92",
      "1.0.0-x-y-z.-"
    ).map(VersionTest::invalidOSGiVersionTestOf);
  }

  /**
   * @return A set of valid version tests
   */

  @TestFactory
  public Stream<DynamicTest> testValidOSGiVersions()
  {
    return Stream.of(
      "1.0.0",
      "1.0.0.SNAPSHOT",
      "1.0.0.alpha",
      "1.0.0.alpha.1",
      "1.0.0.alpha.beta",
      "1.0.0.beta",
      "1.0.0.beta.2",
      "1.0.0.beta.11",
      "1.0.0.rc.1",
      "1.0.0",
      "1.0.0.0.3.7",
      "1.0.0.x.7.z.92",
      "1.0.0.x-y-z.-"
    ).map(VersionTest::validOSGiVersionTestOf);
  }

  private static DynamicTest invalidOSGiVersionTestOf(
    final String text)
  {
    return DynamicTest.dynamicTest(
      "testInvalidVersion_%s".formatted(text),
      () -> {
        final var ex =
          assertThrows(VersionException.class, () -> {
            VersionParser.parseOSGi(text);
          });
        ex.printStackTrace(System.err);
      });
  }

  private static DynamicTest validOSGiVersionTestOf(
    final String text)
  {
    return DynamicTest.dynamicTest(
      "testValidVersion_%s".formatted(text),
      () -> {
        final var p0 = VersionParser.parseOSGi(text);
        final var p1 = VersionParser.parseOSGi(text);
        assertEquals(p0, p1);
        assertEquals(p0, p0);
        assertEquals(p0.hashCode(), p1.hashCode());
        assertEquals(p0.toString(), p1.toString());
        assertEquals(0, p0.compareTo(p1));
        assertNotEquals(p0, Integer.valueOf(23));
      });
  }


  /**
   * @return A set of invalid version tests
   */

  @TestFactory
  public Stream<DynamicTest> testInvalidLaxVersions()
  {
    return Stream.of(
      "",
      "1.0.0-",
      "4294967296.0.0",
      "0.4294967296.0",
      "0.0.4294967296",
      "1.0.0-β",
      "v1.2.3"
    ).map(VersionTest::invalidLaxVersionTestOf);
  }

  /**
   * @return A set of valid version tests
   */

  @TestFactory
  public Stream<DynamicTest> testValidLaxVersions()
  {
    return Stream.of(
      "1.0.0",
      "1.0.0.SNAPSHOT",
      "1.0.0.alpha",
      "1.0.0.alpha.1",
      "1.0.0.alpha.beta",
      "1.0.0.beta",
      "1.0.0.beta.2",
      "1.0.0.beta.11",
      "1.0.0.rc.1",
      "1.0.0",
      "1.0.0.0.3.7",
      "1.0.0.x.7.z.92",
      "1.0.0.x-y-z.-",
      "1.0.0",
      "1.0.0.SNAPSHOT",
      "1.0.0.alpha",
      "1.0.0.alpha.1",
      "1.0.0.alpha.beta",
      "1.0.0.beta",
      "1.0.0.beta.2",
      "1.0.0.beta.11",
      "1.0.0.rc.1",
      "1.0.0",
      "1.0.0.0.3.7",
      "1.0.0.x.7.z.92",
      "1.0.0.x-y-z.-",
      "1.0",
      "1.0-x",
      "1",
      "1-x"
    ).map(VersionTest::validLaxVersionTestOf);
  }

  private static DynamicTest invalidLaxVersionTestOf(
    final String text)
  {
    return DynamicTest.dynamicTest(
      "testInvalidVersion_%s".formatted(text),
      () -> {
        final var ex =
          assertThrows(VersionException.class, () -> {
            VersionParser.parseLax(text);
          });
        ex.printStackTrace(System.err);
      });
  }

  private static DynamicTest validLaxVersionTestOf(
    final String text)
  {
    return DynamicTest.dynamicTest(
      "testValidVersion_%s".formatted(text),
      () -> {
        final var p0 = VersionParser.parseLax(text);
        final var p1 = VersionParser.parseLax(text);
        assertEquals(p0, p1);
        assertEquals(p0, p0);
        assertEquals(p0.hashCode(), p1.hashCode());
        assertEquals(p0.toString(), p1.toString());
        assertEquals(0, p0.compareTo(p1));
        assertNotEquals(p0, Integer.valueOf(23));
      });
  }
}
