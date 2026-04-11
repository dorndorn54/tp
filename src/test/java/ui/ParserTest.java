package ui;

import command.ParsedCommand;
import exception.InvalidCommandException;
import exception.MissingArgumentException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    // -----------------------------------------------------------------------
    // Empty / blank input
    // -----------------------------------------------------------------------

    @Test
    public void parse_emptyString_returnsEmptyCommandWord()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("");
        assertEquals("", cmd.getCommandWord());
    }

    @Test
    public void parse_whitespaceOnly_returnsEmptyCommandWord()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("   ");
        assertEquals("", cmd.getCommandWord());
    }

    @Test
    public void parse_emptyString_returnsNoArgs()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("");
        assertFalse(cmd.hasArg("n"));
    }

    // -----------------------------------------------------------------------
    // Command word extraction
    // -----------------------------------------------------------------------

    @Test
    public void parse_commandWordOnly_returnsCorrectCommandWord()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("listtasks");
        assertEquals("listtasks", cmd.getCommandWord());
    }

    @Test
    public void parse_commandWordOnly_returnsNoArgs()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("listtasks");
        assertFalse(cmd.hasArg("n"));
    }

    @Test
    public void parse_uppercaseCommandWord_normalisesToLowercase()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("ADDSKU n/WIDGET-A1 l/B2");
        assertEquals("addsku", cmd.getCommandWord());
    }

    @Test
    public void parse_mixedCaseCommandWord_normalisesToLowercase()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("AddSku n/WIDGET-A1 l/B2");
        assertEquals("addsku", cmd.getCommandWord());
    }

    @Test
    public void parse_leadingAndTrailingWhitespace_trimsCorrectly()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("  addsku n/WIDGET-A1 l/B2  ");
        assertEquals("addsku", cmd.getCommandWord());
    }

    // -----------------------------------------------------------------------
    // Single flag
    // -----------------------------------------------------------------------

    @Test
    public void parse_singleFlag_parsesKeyAndValue()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addsku n/WIDGET-A1");
        assertEquals("WIDGET-A1", cmd.getArg("n"));
    }

    @Test
    public void parse_singleFlag_absentFlagReturnsNull()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addsku n/WIDGET-A1");
        assertNull(cmd.getArg("l"));
    }

    // -----------------------------------------------------------------------
    // Multiple flags
    // -----------------------------------------------------------------------

    @Test
    public void parse_twoFlags_parsesBothKeyValuePairs()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addsku n/WIDGET-A1 l/B2");
        assertEquals("WIDGET-A1", cmd.getArg("n"));
        assertEquals("B2", cmd.getArg("l"));
    }

    @Test
    public void parse_fourFlags_parsesAllKeyValuePairs()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addskutask n/WIDGET-A1 d/2025-12-31 p/HIGH t/Restock shelves");
        assertEquals("WIDGET-A1", cmd.getArg("n"));
        assertEquals("2025-12-31", cmd.getArg("d"));
        assertEquals("HIGH", cmd.getArg("p"));
        assertEquals("Restock shelves", cmd.getArg("t"));
    }

    @Test
    public void parse_fourFlags_hasArgReturnsTrueForAllPresentFlags()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addskutask n/WIDGET-A1 d/2025-12-31 p/HIGH t/Restock shelves");
        assertTrue(cmd.hasArg("n"));
        assertTrue(cmd.hasArg("d"));
        assertTrue(cmd.hasArg("p"));
        assertTrue(cmd.hasArg("t"));
    }

    // -----------------------------------------------------------------------
    // Flag value edge cases
    // -----------------------------------------------------------------------

    @Test
    public void parse_flagValueWithInternalSpaces_preservesFullValue()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addskutask n/WIDGET-A1 d/2025-12-31 t/urgent restock needed");
        assertEquals("urgent restock needed", cmd.getArg("t"));
    }

    @Test
    public void parse_flagValueWithHyphen_parsesCorrectly()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addsku n/COMPUTER-MOUSE-X1 l/A1");
        assertEquals("COMPUTER-MOUSE-X1", cmd.getArg("n"));
    }

    @Test
    public void parse_flagValueWithNumbers_parsesCorrectly()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("edittask n/WIDGET-A1 i/3 d/2026-01-01");
        assertEquals("3", cmd.getArg("i"));
    }

    @Test
    public void parse_extraWhitespaceBetweenFlags_parsesCorrectly()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("addsku  n/WIDGET-A1   l/B2");
        assertEquals("WIDGET-A1", cmd.getArg("n"));
        assertEquals("B2", cmd.getArg("l"));
    }

    // -----------------------------------------------------------------------
    // Return type
    // -----------------------------------------------------------------------

    @Test
    public void parse_anyValidInput_returnsNonNullParsedCommand()
            throws InvalidCommandException, MissingArgumentException {
        assertNotNull(Parser.parse("help"));
        assertNotNull(Parser.parse("bye"));
        assertNotNull(Parser.parse(""));
    }

    // -----------------------------------------------------------------------
    // Duplicate flags
    // -----------------------------------------------------------------------

    @Test
    public void parse_duplicateFlag_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class,
                () -> Parser.parse("addsku n/FIRST n/SECOND l/A1"));
    }

    // -----------------------------------------------------------------------
    // Real command shapes
    // -----------------------------------------------------------------------

    @Test
    public void parse_deleteSkuCommand_parsesCommandWordAndSkuId()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("deletesku n/WIDGET-A1");
        assertEquals("deletesku", cmd.getCommandWord());
        assertEquals("WIDGET-A1", cmd.getArg("n"));
    }

    @Test
    public void parse_editTaskCommand_parsesAllThreeEditableFields()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("edittask n/WIDGET-A1 i/2 d/2026-06-01 p/LOW t/Updated desc");
        assertEquals("edittask", cmd.getCommandWord());
        assertEquals("WIDGET-A1", cmd.getArg("n"));
        assertEquals("2", cmd.getArg("i"));
        assertEquals("2026-06-01", cmd.getArg("d"));
        assertEquals("LOW", cmd.getArg("p"));
        assertEquals("Updated desc", cmd.getArg("t"));
    }

    @Test
    public void parse_listTasksWithLocationFlag_parsesLocationCorrectly()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("listtasks l/C3");
        assertEquals("listtasks", cmd.getCommandWord());
        assertEquals("C3", cmd.getArg("l"));
    }

    @Test
    public void parse_findCommandWithDescriptionFlag_parsesDescriptionCorrectly()
            throws InvalidCommandException, MissingArgumentException {
        ParsedCommand cmd = Parser.parse("find t/urgent restock");
        assertEquals("find", cmd.getCommandWord());
        assertEquals("urgent restock", cmd.getArg("t"));
    }
}
