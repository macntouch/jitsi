/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.irc;

import junit.framework.*;

/**
 * @author Danny van Heumen
 */
public class UtilsTest
    extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNullText()
    {
        Assert.assertEquals(null, Utils.parse(null));
    }

    public void testParseEmptyString()
    {
        Assert.assertEquals("", Utils.parse(""));
    }

    public void testParseStringWithoutControlCodes()
    {
        final String message = "My normal message without any control codes.";
        Assert.assertEquals(message, Utils.parse(message));
    }

    public void testParseStringWithBoldCode()
    {
        final String ircMessage =
            "My \u0002bold\u0002 message \u0002BOLD!\u0002.";
        final String htmlMessage = "My <b>bold</b> message <b>BOLD!</b>.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithItalicsCode()
    {
        final String ircMessage =
            "My \u0016italics\u0016 message \u0016ITALICS!\u0016.";
        final String htmlMessage = "My <i>italics</i> message <i>ITALICS!</i>.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithUnderlineCode()
    {
        final String ircMessage =
            "My \u001Funderlined\u001F message \u001FUNDERLINED!!!\u001F.";
        final String htmlMessage =
            "My <u>underlined</u> message <u>UNDERLINED!!!</u>.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithForegroundColorCode()
    {
        final String ircMessage = "My \u000304RED\u0003 message.";
        final String htmlMessage = "My <font color=\"Red\">RED</font> message.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithForegroundAndBackgroundColorCode()
    {
        final String ircMessage =
            "My \u000304,12RED on Light Blue\u0003 message.";
        final String htmlMessage =
            "My <font color=\"Red\" bgcolor=\"RoyalBlue\">RED on Light Blue</font> message.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithInvalidBgColorCode()
    {
        final String ircMessage =
            "My \u000304,BRIGHT RED on Light Blue\u0003 message.";
        final String htmlMessage =
            "My <font color=\"Red\">,BRIGHT RED on Light Blue</font> message.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithInvalidColorControlCode()
    {
        final String ircMessage =
            "My \u0003BRIGHT RED on Light Blue\u0003 message.";
        final String htmlMessage =
            "My BRIGHT RED on Light Blue message.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithInvalidSecondControlCode()
    {
        final String ircMessage =
            "My \u000304,12RED on Light Blue\u000304,12 message.";
        final String htmlMessage =
            "My <font color=\"Red\" bgcolor=\"RoyalBlue\">RED on Light Blue<font color=\"Red\" bgcolor=\"RoyalBlue\"> message.</font></font>";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithIncompleteForegroundColorControlCode()
    {
        final String ircMessage = "My \u0003";
        final String htmlMessage = "My ";
        Assert.assertTrue(Utils.parse(ircMessage).startsWith(htmlMessage));
    }

    public void testParseStringWithIncompleteBackgroundColorControlCode()
    {
        final String ircMessage = "My \u000310,";
        final String htmlMessage = "My <font color=\"Teal\">,";
        Assert.assertTrue(Utils.parse(ircMessage).startsWith(htmlMessage));
    }

    public void testParseSringAndNeutralizeWithNormalControlCode()
    {
        final String ircMessage =
            "My \u0002\u0016\u001F\u000304,12RED on Light Blue\u000F message.";
        final String htmlMessage =
            "My <b><i><u><font color=\"Red\" bgcolor=\"RoyalBlue\">RED on Light Blue</font></u></i></b> message.";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseStringWithUnclosedFormattingI()
    {
        final String ircMessage =
            "My \u0002\u0016\u001F\u000304,12RED on Light Blue message.";
        final String htmlMessage =
            "My <b><i><u><font color=\"Red\" bgcolor=\"RoyalBlue\">RED on Light Blue message.</font></u></i></b>";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseUnknownForegroundColor()
    {
        final String ircMessage = "\u000399TEST";
        final String htmlMessage = "99TEST";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }

    public void testParseUnknownBackgroundCOlor()
    {
        final String ircMessage = "\u000300,99TEST";
        final String htmlMessage = "<font color=\"White\">,99TEST</font>";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }
    
    public void testStackIncompatibleFormatToggling()
    {
        final String ircMessage = "\u0002\u0016\u001FHello\u0002 W\u0016orld\u001F!";
        final String htmlMessage = "<b><i><u>Hello</u></i></b><i><u> W</u></i><u>orld</u>!";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }
    
    public void testColorSwitch()
    {
        final String ircMessage = "\u000302,03Hello \u000308,09World\u000F!";
        final String htmlMessage = "<font color=\"Navy\" bgcolor=\"Green\">Hello <font color=\"Yellow\" bgcolor=\"Lime\">World</font></font>!";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }
    
    public void testForegroundColorChange()
    {
        // If only a foreground color is specified, leave the background color active/as is.
        final String ircMessage = "\u000302,03Hello \u000308World\u000F!";
        final String htmlMessage = "<font color=\"Navy\" bgcolor=\"Green\">Hello <font color=\"Yellow\">World</font></font>!";
        Assert.assertEquals(htmlMessage, Utils.parse(ircMessage));
    }
}
