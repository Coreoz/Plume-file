package com.coreoz.plume.file.cleaning;

import javax.annotation.Nullable;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class FileNameCleaning {
    private static final Pattern spacesPattern = Pattern.compile("\\s+");

    private static final Pattern stripAccentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");//$NON-NLS-1$

    private static final String EMPTY = "";

    /**
     * Remove all weird characters while trying to ensure
     * the sanitize file name is close to the original one:
     * - Useless spaces are trimmed
     * - Accents are stripped, spaces are replaced by -,
     * - Upper case chars are converted to lower case.
     *
     * @param fileName the file name, e.g. <code>dog.jpg</code>
     * @return the clean file name, null if the filename is null
     */
    @Nullable
    public static String cleanFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        return spacesPattern.matcher(stripAccents(fileName.trim().toLowerCase())).replaceAll("-");
    }

    /**
     * <p>Removes diacritics (~= accents) from a string. The case will not be altered.</p>
     * <p>For instance, '&agrave;' will be replaced by 'a'.</p>
     * <p>Note that ligatures will be left as is.</p>
     *
     * <pre>
     * StringUtils.stripAccents(null)                = null
     * StringUtils.stripAccents("")                  = ""
     * StringUtils.stripAccents("control")           = "control"
     * StringUtils.stripAccents("&eacute;clair")     = "eclair"
     * </pre>
     *
     * @param input String to be stripped
     * @return input text with diacritics removed
     * @since 3.0
     * @author <a href="https://commons.apache.org/proper/commons-lang/">Apache Software Foundation</a>
     */
    // See also Lucene's ASCIIFoldingFilter (Lucene 2.9) that replaces accented characters by their unaccented equivalent (and uncommitted bug fix: https://issues.apache.org/jira/browse/LUCENE-1343?focusedCommentId=12858907&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_12858907).
    private static String stripAccents(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        convertRemainingAccentCharacters(decomposed);
        // Note that this doesn't correctly remove ligatures...
        return stripAccentPattern.matcher(decomposed).replaceAll(EMPTY);
    }

    private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
        for (int i = 0; i < decomposed.length(); i++) {
            if (decomposed.charAt(i) == '\u0141') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (decomposed.charAt(i) == '\u0142') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }
}
