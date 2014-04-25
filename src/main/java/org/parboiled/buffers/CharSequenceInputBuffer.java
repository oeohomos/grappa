package org.parboiled.buffers;

import com.google.common.base.Preconditions;
import org.parboiled.common.IntArrayStack;
import org.parboiled.support.Chars;
import org.parboiled.support.IndexRange;
import org.parboiled.support.Position;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link InputBuffer} over a {@link CharSequence}
 *
 * <p>A {@link CharSequence} is the most basic character interface in the JDK.
 * It is implemented by a lot of character related classes, including {@link
 * String}, and is also the argument type used by a {@link Pattern}'s {@link
 * Matcher}.</p>
 *
 * <p>Among other things, this means you can use this package on very large
 * files using <a href="https://github.com/fge/largetext">largetext</a>, which
 * implements {@link CharSequence} over multi-gigabyte files.</p>
 */
@Immutable
public final class CharSequenceInputBuffer
    implements InputBuffer
{
    private final CharSequence charSequence;

    private final AtomicBoolean newlinesAreBuilt
        = new AtomicBoolean(false);
    @GuardedBy("newlinesAreBuilt")
    private int[] newlines;

    public CharSequenceInputBuffer(final CharSequence charSequence)
    {
        this.charSequence = charSequence;
    }

    @Override
    public char charAt(final int index)
    {
        return index >= 0 && index < charSequence.length()
            ? charSequence.charAt(index) : Chars.EOI;
    }

    @Override
    public boolean test(final int index, final char[] characters)
    {
        final int length = characters.length;
        if (index + length > charSequence.length())
            return false;
        for (int i = 0; i < length; i++)
            if (charSequence.charAt(index + i) != characters[i])
                return false;
        return true;
    }

    @Override
    public String extract(final int start, final int end)
    {
        return charSequence.subSequence(start, end).toString();
    }

    @Override
    public String extract(final IndexRange range)
    {
        return extract(range.start, range.end);
    }

    @Override
    public Position getPosition(final int index)
    {
        if (!newlinesAreBuilt.getAndSet(true))
            buildNewlines();
        final int line = getLineNumber(newlines, index);
        final int column = line == 0 ? index + 1
            : index - newlines[line - 1];
        return new Position(line + 1, column);
    }

    @Override
    public int getOriginalIndex(final int index)
    {
        return index;
    }

    @Override
    public String extractLine(final int lineNumber)
    {
        if (!newlinesAreBuilt.getAndSet(true))
            buildNewlines();
        Preconditions.checkArgument(lineNumber > 0, "line number is negative");
        Preconditions.checkArgument(lineNumber <= newlines.length + 1,
            "line index out of range");
        final int start = lineNumber > 1 ? newlines[lineNumber - 2] + 1 : 0;
        int end = lineNumber <= newlines.length ? newlines[lineNumber - 1]
            : charSequence.length();
        if (charAt(end - 1) == '\r') end--;
        return extract(start, end);
    }

    @Override
    public int getLineCount()
    {
        return 0;
    }

    private void buildNewlines()
    {
        final IntArrayStack stack = new IntArrayStack();
        for (int i = 0; i < charSequence.length(); i++)
            if (charSequence.charAt(i) == '\n')
                stack.push(i);
        newlines = new int[stack.size()];
        stack.getElements(newlines, 0);
    }

    private static int getLineNumber(final int[] newlines, final int index)
    {
        final int ret = Arrays.binarySearch(newlines, index);
        return ret >= 0 ? ret : -(ret + 1);
    }
}
