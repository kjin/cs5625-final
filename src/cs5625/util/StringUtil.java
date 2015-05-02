/*
 *
 *  * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  * Copyright (c) 2015, Department of Computer Science, Cornell University.
 *  *
 *  * This code repository has been authored collectively by:
 *  * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  * Pramook Khungurn (pk395), Steve Marschner (srm2), and Sean Ryan (ser99)
 *
 */

package cs5625.util;

import java.util.ArrayList;

public class StringUtil {
    /**
     * Splits the passed string into words separated by any one of a list of characters.
     *
     * This method is faster than String.split(), which uses regular expressions. We don't
     * need regex splitting here.
     *
     * @param str The string to split.
     * @param delims Words in the string are separated by any of these.
     * @param keepEmptyWords If true, empty words (between consecutive delimeter characters) are
     *        included in the result array.
     *
     * @return Array of words separated by characters in delims.
     */
    public static String[] splitString(String str, String delims, boolean keepEmptyWords)
    {
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();

        int offset = 0;

		/* Loop over characters in the string. */
        while (offset < str.length())
        {
            char c = str.charAt(offset);

            if (delims.indexOf(c) < 0)
            {
				/* If this isn't in the delimeters list, add it to the current word. */
                builder.append(c);
            }
            else
            {
				/* This is a delimeter, so add the current word, if any, to the results list,
				 * and clear the builder. */
                if (builder.length() > 0 || keepEmptyWords)
                {
                    result.add(builder.toString());
                    builder.delete(0, builder.length());
                }
            }

			/* Next character. */
            ++offset;
        }

		/* Add the last word, if any. */
        if (builder.length() > 0 || keepEmptyWords)
        {
            result.add(builder.toString());
        }

        return result.toArray(new String[]{});
    }
}
