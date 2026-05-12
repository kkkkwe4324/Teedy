package com.sismics.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for HtmlToPlainText.
 */
public class TestHtmlToPlainText {

    @Test
    public void testListDefinitionParagraphAndAnchorFormatting() {
        String html = ""
                + "<h1>Title</h1>"
                + "<p>Paragraph text</p>"
                + "<ul><li>First item</li><li>Second item</li></ul>"
                + "<dl><dt>Term</dt><dd>Definition</dd></dl>"
                + "<a href='https://example.com/path'>Example Link</a>"
                + "<br/>After break";

        Document doc = Jsoup.parse(html, "https://example.com");
        HtmlToPlainText converter = new HtmlToPlainText();

        String plain = converter.getPlainText(doc.body());

        // Heading / paragraph line starts
        Assert.assertTrue(plain.contains("Title"));
        Assert.assertTrue(plain.contains("Paragraph text"));

        // List branch: li => "\n * "
        Assert.assertTrue(plain.contains("* First item"));
        Assert.assertTrue(plain.contains("* Second item"));

        // Definition list branches: dt head "  ", dd/dt tail "\n"
        Assert.assertTrue(plain.contains("  Term"));
        Assert.assertTrue(plain.contains("Definition"));

        // Anchor tail branch appends absolute URL
        Assert.assertTrue(plain.contains("Example Link <https://example.com/path>"));

        // br tail branch appends newline, then content should exist
        Assert.assertTrue(plain.contains("After break"));
    }

    @Test
    public void testTableRowAndWrapAtMaxWidth() {
        String longSentence =
                "This is a very long sentence that should be wrapped by the formatter because it " +
                "clearly exceeds eighty characters in a single line of output.";

        String html = ""
                + "<table><tr><td>RowOne</td></tr><tr><td>RowTwo</td></tr></table>"
                + "<p>" + longSentence + "</p>";

        Document doc = Jsoup.parse(html);
        HtmlToPlainText converter = new HtmlToPlainText();

        String plain = converter.getPlainText(doc.body());

        // tr branch should cause new lines
        Assert.assertTrue(plain.contains("RowOne"));
        Assert.assertTrue(plain.contains("RowTwo"));

        // wrap branch: line should be broken for long sentence
        String[] lines = plain.split("\\n");
        boolean hasWrappedLine = false;
        for (String line : lines) {
            if (line.length() > 0 && line.length() <= 90 && line.contains("wrapped by the formatter")) {
                hasWrappedLine = true;
                break;
            }
        }
        Assert.assertTrue("Expected wrapped content line", hasWrappedLine);

        // Ensure at least one newline exists from wrapping/formatting
        Assert.assertTrue(plain.contains("\n"));
    }

    @Test
    public void testSpaceHandlingAndNonEmptyOutput() {
        String html = "<p>alpha     beta</p><p>gamma</p>";
        Document doc = Jsoup.parse(html);
        HtmlToPlainText converter = new HtmlToPlainText();

        String plain = converter.getPlainText(doc.body());

        // Basic content check
        Assert.assertTrue(plain.contains("alpha"));
        Assert.assertTrue(plain.contains("beta"));
        Assert.assertTrue(plain.contains("gamma"));

        // Should not start as empty and should contain line breaks from paragraph formatting
        Assert.assertFalse(plain.trim().isEmpty());
        Assert.assertTrue(plain.contains("\n"));
    }
}