/*
 * Copyright © 2026 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.verona.tests.arbitraries;

import com.io7m.verona.core.VersionQualifier;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * A provider of {@link VersionQualifier}.
 */

public final class VersionQualifierArb implements ArbitraryProvider
{
  /**
   * A provider of {@link VersionQualifier}.
   */

  public VersionQualifierArb()
  {

  }

  @Override
  public boolean canProvideFor(
    final TypeUsage typeUsage)
  {
    return typeUsage.isOfType(VersionQualifier.class);
  }

  @Override
  public Set<Arbitrary<?>> provideFor(
    final TypeUsage typeUsage,
    final SubtypeProvider subtypeProvider)
  {
    return Set.of(
      Arbitraries.strings()
        .withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890.-")
        .filter(VersionQualifierArb::matchesQualifier)
        .map(VersionQualifier::new)
    );
  }

  private static final Pattern VALID_QUALIFIER =
    Pattern.compile("[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*");

  private static boolean matchesQualifier(
    final String x)
  {
    return VALID_QUALIFIER.matcher(x).matches();
  }
}
