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

package com.io7m.verona.core;

import java.util.Objects;

/**
 * A version range.
 *
 * @param lower          The lower version
 * @param lowerInclusive {@code true} if the lower version bound is inclusive
 * @param upper          The upper version
 * @param upperInclusive {@code true} if the upper version bound is inclusive
 */

public record VersionRange(
  Version lower,
  boolean lowerInclusive,
  Version upper,
  boolean upperInclusive)
{
  /**
   * A version range.
   *
   * @param lower          The lower version
   * @param lowerInclusive {@code true} if the lower version bound is inclusive
   * @param upper          The upper version
   * @param upperInclusive {@code true} if the upper version bound is inclusive
   */

  public VersionRange
  {
    Objects.requireNonNull(lower, "lower");
    Objects.requireNonNull(upper, "upper");

    if (lower.compareTo(upper) > 0) {
      throw new IllegalArgumentException(
        "Version %s must be <= version %s".formatted(lower, upper)
      );
    }
  }

  @Override
  public String toString()
  {
    final var builder = new StringBuilder(128);
    builder.append(this.lowerInclusive ? '[' : '(');
    builder.append(this.lower);
    builder.append(", ");
    builder.append(this.upper);
    builder.append(this.upperInclusive ? ']' : ')');
    return builder.toString();
  }

  /**
   * @param version The version
   *
   * @return {@code true} if this version range contains {@code version}
   */

  public boolean contains(
    final Version version)
  {
    Objects.requireNonNull(version, "version");

    final var lowerOk =
      this.lowerInclusive
        ? version.compareTo(this.lower) >= 0
        : version.compareTo(this.lower) > 0;
    final var upperOk =
      this.upperInclusive
        ? version.compareTo(this.upper) <= 0
        : version.compareTo(this.upper) < 0;

    return lowerOk && upperOk;
  }
}
