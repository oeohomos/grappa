/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fge.grappa.matchers.unicode;

import com.github.fge.grappa.matchers.CharMatcher;
import org.parboiled.MatcherContext;

/**
 * A {@link UnicodeCharMatcher} for characters inside the basic multilingual
 * plane
 *
 * <p>The basic multilingual plane includes all code points in the range U+0000
 * to U+FFFF, as far as Java is concerned, there is a one-to-one match between
 * such a code point and a {@code char}, so what this does is simply delegate
 * the job to a {@link CharMatcher}.</p>
 */
public final class BmpCharMatcher
    extends UnicodeCharMatcher
{
    private final CharMatcher matcher;

    BmpCharMatcher(final String label, final char c)
    {
        super(label);
        matcher = new CharMatcher(c);
    }

    @Override
    public <V> boolean match(final MatcherContext<V> context)
    {
        return matcher.match(context);
    }
}