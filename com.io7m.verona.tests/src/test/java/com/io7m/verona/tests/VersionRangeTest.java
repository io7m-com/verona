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
import com.io7m.verona.core.VersionRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class VersionRangeTest
{
  /**
   * Lower bounds must be less than or equal to upper bounds.
   */

  @Test
  public void testMalformedRange()
  {
    final var v0 =
      Version.of(1, 0, 0);
    final var v1 =
      Version.of(0, 0, 0);

    final var ex =
      assertThrows(IllegalArgumentException.class, () -> {
        new VersionRange(
          v0,
          true,
          v1,
          true
        );
      });
  }

  /**
   * All minor and patch versions are contained in a major range.
   *
   * @param minor The minor version
   * @param patch The patch version
   */

  @Property
  public void testContains0(
    final @ForAll int minor,
    final @ForAll int patch)
  {
    final var range =
      new VersionRange(
        Version.of(1, 0, 0),
        true,
        Version.of(2, 0, 0),
        false
      );

    assertTrue(
      range.contains(Version.of(1, minor, patch))
    );
  }

  /**
   * All versions are contained in a non-inclusive range.
   *
   * @param major The major version
   * @param minor The minor version
   * @param patch The patch version
   */

  @Property
  public void testContains1(
    final @ForAll @IntRange(min = 2, max = 99) int major,
    final @ForAll int minor,
    final @ForAll int patch)
  {
    final var range =
      new VersionRange(
        Version.of(1, 0, 0),
        false,
        Version.of(100, 0, 0),
        false
      );

    assertTrue(
      range.contains(Version.of(major, minor, patch))
    );
  }

  /**
   * All versions are contained in an inclusive range.
   *
   * @param major The major version
   */

  @Property
  public void testContains2(
    final @ForAll @IntRange(min = 1, max = 100) int major)
  {
    final var range =
      new VersionRange(
        Version.of(1, 0, 0),
        true,
        Version.of(100, 0, 0),
        true
      );

    assertTrue(
      range.contains(Version.of(major, 0, 0))
    );
  }

  /**
   * toString() works.
   */

  @Test
  public void testToString0()
  {
    assertEquals(
      "[1.0.0, 2.0.0]",
      new VersionRange(
        Version.of(1, 0, 0),
        true,
        Version.of(2, 0, 0),
        true
      ).toString()
    );
  }

  /**
   * toString() works.
   */

  @Test
  public void testToString1()
  {
    assertEquals(
      "(1.0.0, 2.0.0]",
      new VersionRange(
        Version.of(1, 0, 0),
        false,
        Version.of(2, 0, 0),
        true
      ).toString()
    );
  }

  /**
   * toString() works.
   */

  @Test
  public void testToString2()
  {
    assertEquals(
      "[1.0.0, 2.0.0)",
      new VersionRange(
        Version.of(1, 0, 0),
        true,
        Version.of(2, 0, 0),
        false
      ).toString()
    );
  }

  /**
   * toString() works.
   */

  @Test
  public void testToString3()
  {
    assertEquals(
      "(1.0.0, 2.0.0)",
      new VersionRange(
        Version.of(1, 0, 0),
        false,
        Version.of(2, 0, 0),
        false
      ).toString()
    );
  }

  /**
   * toString() works.
   */

  @Test
  public void testToString4()
  {
    assertEquals(
      "[1.0.0-SNAPSHOT, 2.0.0-SNAPSHOT]",
      new VersionRange(
        Version.of(1, 0, 0, "SNAPSHOT"),
        true,
        Version.of(2, 0, 0, "SNAPSHOT"),
        true
      ).toString()
    );
  }
}
