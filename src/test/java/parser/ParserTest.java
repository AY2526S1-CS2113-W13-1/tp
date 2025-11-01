package parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import seedu.zettel.commands.Command;
import seedu.zettel.commands.DeleteNoteCommand;
import seedu.zettel.commands.DeleteTagFromNoteCommand;
import seedu.zettel.commands.DeleteTagGloballyCommand;
import seedu.zettel.commands.EditNoteCommand;
import seedu.zettel.commands.ExitCommand;
import seedu.zettel.commands.FindNoteByBodyCommand;
import seedu.zettel.commands.FindNoteByTitleCommand;
import seedu.zettel.commands.HelpCommand; // Added import
import seedu.zettel.commands.InitCommand;
import seedu.zettel.commands.LinkBothNotesCommand;
import seedu.zettel.commands.LinkNotesCommand;
import seedu.zettel.commands.ListLinkedNotesCommand;
import seedu.zettel.commands.ListNoteCommand;
import seedu.zettel.commands.ListTagsGlobalCommand;
import seedu.zettel.commands.ListTagsSingleNoteCommand;
import seedu.zettel.commands.NewNoteCommand;
import seedu.zettel.commands.NewTagCommand;
import seedu.zettel.commands.PinNoteCommand;
import seedu.zettel.commands.RenameTagCommand;
import seedu.zettel.commands.PrintNoteBodyCommand;
import seedu.zettel.commands.TagNoteCommand;
import seedu.zettel.commands.UnlinkBothNotesCommand;
import seedu.zettel.commands.UnlinkNotesCommand;
import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.InvalidInputException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.parser.Parser;

/**
 * Unit tests for the Parser class.
 * Tests parsing of all commands and validation of hexadecimal note IDs.
 */
class ParserTest {

    @Test
    void testParseExitCommandReturnsExitCommand() throws ZettelException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    void testParseListCommandReturnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void testParseListWithPinnedFlagReturnsListNoteCommand() throws ZettelException {
        Command command = Parser.parse("list -p");
        assertInstanceOf(ListNoteCommand.class, command);
    }

    @Test
    void testParseListWithInvalidFlagThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list -x"));
    }

    @Test
    void testParseNewNoteWithTitleOnlyReturnsNewNoteCommand() throws ZettelException {
        Command command = Parser.parse("new -t My Title");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void testParseNewNoteWithTitleAndBodyReturnsNewNoteCommand() throws ZettelException {
        Command command = Parser.parse("new -t My Title -b My Body");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void testParseNewNoteWithoutTitleFlagThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("new My Title"));
    }

    @Test
    void testParseNewNoteWithEmptyTitleThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("new -t"));
    }

    @Test
    void testParseDeleteWithValidHexIdReturnsDeleteNoteCommand() throws ZettelException {
        Command command = Parser.parse("delete abcd1234");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void testParseDeleteWithUppercaseHexThrowsInvalidFormatException() {
        // IDs must be lowercase hex only
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete ABCD1234"));
    }

    @Test
    void testParseDeleteWithSpecialCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abcd-234"));
    }

    @Test
    void testParseDeleteWithInvalidCharactersThrowsInvalidFormatException() {
        // 'g' is not a valid hex character
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abcdefgh"));
    }

    @Test
    void testParseDeleteWithForceFlagReturnsDeleteNoteCommand() throws ZettelException {
        Command command = Parser.parse("delete -f abcd1234");
        assertInstanceOf(DeleteNoteCommand.class, command);
    }

    @Test
    void testParseDeleteWithoutIdThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("delete"));
    }

    @Test
    void testParseDeleteWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abc"));
    }

    @Test
    void testParseDeleteWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete abcdef123"));
    }

    @Test
    void testParsePinWithValidHexIdReturnsPinNoteCommand() throws ZettelException {
        Command command = Parser.parse("pin abcd1234");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void testParseUnpinWithValidHexIdReturnsPinNoteCommand() throws ZettelException {
        Command command = Parser.parse("unpin def56789");
        assertInstanceOf(PinNoteCommand.class, command);
    }

    @Test
    void testParsePinWithoutIdThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("pin"));
    }

    @Test
    void testParsePinWithSpecialCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin abcd-234"));
    }

    @Test
    void testParsePinWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin abc"));
    }

    @Test
    void testParsePinWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin abcdef123"));
    }

    @Test
    void testParsePinWithUppercaseHexThrowsInvalidFormatException() {
        // IDs must be lowercase hex only
        assertThrows(InvalidFormatException.class, () -> Parser.parse("pin ABCD1234"));
    }

    @Test
    void testParseInitWithRepoNameReturnsInitCommand() throws ZettelException {
        Command command = Parser.parse("init myRepo");
        assertInstanceOf(InitCommand.class,command);
    }

    @Test
    void testParseInitWithoutRepoNameThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("init"));
    }

    @Test
    void testParseInitWithInvalidFormatThrowsInvalidInputException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("init my repo!"));
    }

    @Test
    void testParseFindWithSearchTermReturnsFindNoteCommand() throws ZettelException {
        Command command = Parser.parse("find-note-by-body test");
        assertInstanceOf(FindNoteByBodyCommand.class, command);
    }

    @Test
    void testParseFindWithoutSearchTermThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("find-note-by-body"));
    }

    @Test
    void testParseFindByTitleWithSearchTermReturnsFindNoteByTitleCommand() throws ZettelException {
        Command command = Parser.parse("find-note-by-title test");
        assertInstanceOf(FindNoteByTitleCommand.class, command);
    }

    @Test
    void testParseFindByTitleWithMultipleTermsReturnsFindNoteByTitleCommand() throws ZettelException {
        Command command = Parser.parse("find-note-by-title title1 title2");
        assertInstanceOf(FindNoteByTitleCommand.class, command);
    }

    @Test
    void testParseFindByTitleWithoutSearchTermThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parse("find-note-by-title"));
    }

    // ==================== Link Command Tests ====================

    @Test
    void testParseLinkWithValidIdsReturnsLinkNotesCommand() throws ZettelException {
        Command command = Parser.parse("link abcd1234 ef567890");
        assertInstanceOf(LinkNotesCommand.class, command);
    }

    @Test
    void testParseLinkWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link"));
    }

    @Test
    void testParseLinkWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234"));
    }

    @Test
    void testParseLinkWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseLinkWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abc ef567890"));
    }

    @Test
    void testParseLinkWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef5"));
    }

    @Test
    void testParseLinkWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd12345 ef567890"));
    }

    @Test
    void testParseLinkWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef567890123"));
    }

    @Test
    void testParseLinkWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd-234 ef567890"));
    }

    @Test
    void testParseLinkWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ef56@890"));
    }

    @Test
    void testParseLinkWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link ABCD1234 ef567890"));
    }

    @Test
    void testParseLinkWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 EF567890"));
    }

    @Test
    void testParseLinkWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcdefgh ef567890"));
    }

    @Test
    void testParseLinkWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link abcd1234 ghijk890"));
    }

    // ==================== Tag Command Tests ====================

    @Test
    void testParseAddTagWithValidIdAndTagReturnsTagNoteCommand() throws ZettelException {
        Command command = Parser.parse("add-tag abcd1234 urgent");
        assertInstanceOf(TagNoteCommand.class, command);
    }

    @Test
    void testParseAddTagWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag"));
    }

    @Test
    void testParseAddTagWithOnlyNoteIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag abcd1234"));
    }

    @Test
    void testParseAddTagWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag abc urgent"));
    }

    @Test
    void testParseAddTagWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag abcd12345 urgent"));
    }

    @Test
    void testParseAddTagWithSpecialCharactersIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag abcd-234 urgent"));
    }

    @Test
    void testParseAddTagWithUppercaseIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag ABCD1234 urgent"));
    }

    @Test
    void testParseAddTagWithInvalidHexCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("add-tag abcdefgh urgent"));
    }

    @Test
    void testParseUnknownCommandThrowsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Parser.parse("unknown"));
    }

    @Test
    void testParseEditWithValidHexIdReturnsEditNoteCommand() throws ZettelException {
        Command command = Parser.parse("edit abcd1234");
        assertInstanceOf(EditNoteCommand.class, command);
    }

    @Test
    void testParseEditWithoutIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit"));
    }

    @Test
    void testParseEditWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit abc"));
    }

    @Test
    void testParseEditWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit abcdef123"));
    }

    @Test
    void testParseEditWithUppercaseHexThrowsInvalidFormatException() {
        // IDs must be lowercase hex only
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit ABCD1234"));
    }

    @Test
    void testParseEditWithSpecialCharactersThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit abcd-234"));
    }

    @Test
    void testParseEditWithInvalidCharactersThrowsInvalidFormatException() {
        // 'g' is not a valid hex character
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit abcdefgh"));
    }

    @Test
    void testParseEditWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("edit abcd1234 extra"));
    }

    @Test
    void testParseNewNoteWithEmptyBodyFlagReturnsNewNoteCommand() throws ZettelException {
        // "new -t Title -b" should parse successfully with empty body string
        Command command = Parser.parse("new -t Title -b");
        assertInstanceOf(NewNoteCommand.class, command);
    }

    @Test
    void testParseNewNoteWithTitleOnlyCreatesCommandWithNullBody() throws ZettelException {
        // "new -t Title" (no -b flag) should create command with null body
        // This triggers editor opening behavior
        Command command = Parser.parse("new -t Title");
        assertInstanceOf(NewNoteCommand.class, command);
    }
    // ==================== Unlink Command Tests ====================

    @Test
    void testParseUnlinkWithValidIdsReturnsUnlinkNotesCommand() throws ZettelException {
        Command command = Parser.parse("unlink abcd1234 ef567890");
        assertInstanceOf(UnlinkNotesCommand.class, command);
    }

    @Test
    void testParseUnlinkWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink"));
    }

    @Test
    void testParseUnlinkWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234"));
    }

    @Test
    void testParseUnlinkWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseUnlinkWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abc ef567890"));
    }

    @Test
    void testParseUnlinkWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef5"));
    }

    @Test
    void testParseUnlinkWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd12345 ef567890"));
    }

    @Test
    void testParseUnlinkWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef567890123"));
    }

    @Test
    void testParseUnlinkWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd-234 ef567890"));
    }

    @Test
    void testParseUnlinkWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ef56@890"));
    }

    @Test
    void testParseUnlinkWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink ABCD1234 ef567890"));
    }

    @Test
    void testParseUnlinkWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 EF567890"));
    }

    @Test
    void testParseUnlinkWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcdefgh ef567890"));
    }

    @Test
    void testParseUnlinkWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink abcd1234 ghijk890"));
    }

    // ==================== Link-Both Command Tests ====================

    @Test
    void testParseLinkBothWithValidIdsReturnsLinkBothNotesCommand() throws ZettelException {
        Command command = Parser.parse("link-both abcd1234 ef567890");
        assertInstanceOf(LinkBothNotesCommand.class, command);
    }

    @Test
    void testParseLinkBothWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both"));
    }

    @Test
    void testParseLinkBothWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234"));
    }

    @Test
    void testParseLinkBothWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseLinkBothWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abc ef567890"));
    }

    @Test
    void testParseLinkBothWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef5"));
    }

    @Test
    void testParseLinkBothWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd12345 ef567890"));
    }

    @Test
    void testParseLinkBothWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef567890123"));
    }

    @Test
    void testParseLinkBothWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd-234 ef567890"));
    }

    @Test
    void testParseLinkBothWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ef56@890"));
    }

    @Test
    void testParseLinkBothWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both ABCD1234 ef567890"));
    }

    @Test
    void testParseLinkBothWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 EF567890"));
    }

    @Test
    void testParseLinkBothWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcdefgh ef567890"));
    }

    @Test
    void testParseLinkBothWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("link-both abcd1234 ghijk890"));
    }

    // ==================== Unlink-Both Command Tests ====================

    @Test
    void testParseUnlinkBothWithValidIdsReturnsUnlinkBothNotesCommand() throws ZettelException {
        Command command = Parser.parse("unlink-both abcd1234 ef567890");
        assertInstanceOf(UnlinkBothNotesCommand.class, command);
    }

    @Test
    void testParseUnlinkBothWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both"));
    }

    @Test
    void testParseUnlinkBothWithOnlyOneIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234"));
    }

    @Test
    void testParseUnlinkBothWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef567890 12345678"));
    }

    @Test
    void testParseUnlinkBothWithTooShortFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abc ef567890"));
    }

    @Test
    void testParseUnlinkBothWithTooShortSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef5"));
    }

    @Test
    void testParseUnlinkBothWithTooLongFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd12345 ef567890"));
    }

    @Test
    void testParseUnlinkBothWithTooLongSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef567890123"));
    }

    @Test
    void testParseUnlinkBothWithSpecialCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd-234 ef567890"));
    }

    @Test
    void testParseUnlinkBothWithSpecialCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ef56@890"));
    }

    @Test
    void testParseUnlinkBothWithUppercaseFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both ABCD1234 ef567890"));
    }

    @Test
    void testParseUnlinkBothWithUppercaseSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 EF567890"));
    }

    @Test
    void testParseUnlinkBothWithInvalidHexCharactersFirstIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcdefgh ef567890"));
    }

    @Test
    void testParseUnlinkBothWithInvalidHexCharactersSecondIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("unlink-both abcd1234 ghijk890"));
    }

    @Test
    void testParseNewTagWithValidTagReturnsNewTagCommand() throws ZettelException {
        Command command = Parser.parse("new-tag urgent");
        assertInstanceOf(NewTagCommand.class, command);
    }

    @Test
    void testParseNewTagWithoutTagThrowsEmptyDescriptionException() {
        ZettelException ex = assertThrows(ZettelException.class, () -> Parser.parse("new-tag"));
        assertTrue(ex.getMessage().contains("Tag"));
    }

    @Test
    void testParseNewTagWithEmptyTagThrowsEmptyDescriptionException() {
        ZettelException ex = assertThrows(ZettelException.class, () -> Parser.parse("new-tag "));
        assertTrue(ex.getMessage().contains("Tag"));
    }

    @Test
    void testParseListTagsGlobalWithValidCommandReturnsListTagsGlobalCommand() throws ZettelException {
        Command command = Parser.parse("list-tags-all");
        assertInstanceOf(ListTagsGlobalCommand.class, command);
    }

    @Test
    void testParseListTagsGlobalWithExtraArgumentsThrowsInvalidFormatException() {
        ZettelException ex = assertThrows(ZettelException.class, () -> Parser.parse("list-tags-all extra"));
        assertTrue(ex.getMessage().contains("list-tags-all"));
    }

    //@@author danielkwan2004-reused
    //Reused from testParseUnlinkBoth tests above with modifications
    //list-tags command parser tests
    @Test
    void testParseListTagsSingleNoteWithValidIdReturnsListTagsSingleNoteCommand() throws ZettelException {
        Command command = Parser.parse("list-tags abcd1234");
        assertTrue(command instanceof ListTagsSingleNoteCommand);
    }

    @Test
    void testParseListTagsSingleNoteWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags"));
    }

    @Test
    void testParseListTagsSingleNoteWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abcd1234 ef567890"));
    }

    @Test
    void testParseListTagsSingleNoteWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abc123"));
    }

    @Test
    void testParseListTagsSingleNoteWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abcd12345"));
    }

    @Test
    void testParseListTagsSingleNoteWithSpecialCharactersIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abcd@234"));
    }

    @Test
    void testParseListTagsSingleNoteWithUppercaseIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags ABCD1234"));
    }

    @Test
    void testParseListTagsSingleNoteWithInvalidHexCharactersIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abcdefgh"));
    }

    @Test
    void testParseListTagsSingleNoteWithWhitespaceInIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abc 1234"));
    }

    @Test
    void testParseListTagsSingleNoteWithEmptyStringIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags "));
    }

    @Test
    void testParseListTagsSingleNoteWithMixedCaseIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags AbCd1234"));
    }

    @Test
    void testParseListTagsSingleNoteWithSymbolsIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-tags abcd#234"));
    }

    @Test
    void testParseListTagsSingleNoteWithNumbersOnlyIdReturnsListTagsSingleNoteCommand() throws ZettelException {
        Command command = Parser.parse("list-tags 12345678");
        assertTrue(command instanceof ListTagsSingleNoteCommand);
    }

    @Test
    void testParseListTagsSingleNoteWithLettersOnlyIdReturnsListTagsSingleNoteCommand() throws ZettelException {
        Command command = Parser.parse("list-tags abcdefab");
        assertTrue(command instanceof ListTagsSingleNoteCommand);
    }
    //@@author

    //@@author danielkwan2004-reused
    //Reused from testParseListTagsSingleNote tests above with modifications
    //delete-tag command parser tests
    @Test
    void testParseDeleteTagFromNoteWithValidIdAndTagReturnsDeleteTagFromNoteCommand() 
            throws ZettelException {
        Command command = Parser.parse("delete-tag abcd1234 java");
        assertTrue(command instanceof DeleteTagFromNoteCommand);
    }

    @Test
    void testParseDeleteTagFromNoteWithForceFlagReturnsDeleteTagFromNoteCommand()
            throws ZettelException {
        Command command = Parser.parse("delete-tag -f abcd1234 java");
        assertTrue(command instanceof DeleteTagFromNoteCommand);
    }

    @Test
    void testParseDeleteTagFromNoteWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag"));
    }

    @Test
    void testParseDeleteTagFromNoteWithOnlyNoteIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abcd1234"));
    }

    @Test
    void testParseDeleteTagFromNoteWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abcd1234 java extra"));
    }

    @Test
    void testParseDeleteTagFromNoteWithTooShortIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abc1234 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithTooLongIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abcd12345 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithSpecialCharactersIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abcd@234 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithUppercaseIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag ABCD1234 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithInvalidHexCharactersIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abcdxyz1 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithMixedCaseIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag AbCd1234 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithWhitespaceInIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag abcd 1234 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithEmptyStringIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag \"\" java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithSymbolsIdThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag #abcd123 java"));
    }

    @Test
    void testParseDeleteTagFromNoteWithNumbersOnlyIdReturnsDeleteTagFromNoteCommand() 
            throws ZettelException {
        Command command = Parser.parse("delete-tag 12345678 java");
        assertTrue(command instanceof DeleteTagFromNoteCommand);
    }

    @Test
    void testParseDeleteTagFromNoteWithLettersOnlyIdReturnsDeleteTagFromNoteCommand() 
            throws ZettelException {
        Command command = Parser.parse("delete-tag abcdefab java");
        assertTrue(command instanceof DeleteTagFromNoteCommand);
    }
    
    // delete-tag-globally command parser tests
    @Test
    void testParseDeleteTagGloballyWithValidTagReturnsDeleteTagGloballyCommand() throws ZettelException {
        Command command = Parser.parse("delete-tag-globally common");
        assertTrue(command instanceof DeleteTagGloballyCommand);
    }

    @Test
    void testParseDeleteTagGloballyWithForceFlagReturnsDeleteTagGloballyCommand() throws ZettelException {
        Command command = Parser.parse("delete-tag-globally -f common");
        assertTrue(command instanceof DeleteTagGloballyCommand);
    }

    @Test
    void testParseDeleteTagGloballyWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag-globally"));
    }

    @Test
    void testParseDeleteTagGloballyWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("delete-tag-globally common extra"));
    }
    // ==================== List Linked (Direct) Command Tests ====================

    @Test
    void testParseListIncomingLinksWithValidIdReturnsListLinkedNotesCommand() throws ZettelException {
        Command command = Parser.parse("list-incoming-links abcd1234");
        assertInstanceOf(ListLinkedNotesCommand.class, command);
    }

    @Test
    void testParseListIncomingLinksWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-incoming-links"));
    }

    @Test
    void testParseListIncomingLinksWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-incoming-links abcd1234 extra"));
    }

    @Test
    void testParseListIncomingLinksWithInvalidIdThrowsInvalidFormatException() {
        // Uppercase and non-hex should fail
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-incoming-links ABCD1234"));
    }

    @Test
    void testParseListOutgoingLinksWithValidIdReturnsListLinkedNotesCommand() throws ZettelException {
        Command command = Parser.parse("list-outgoing-links abcd1234");
        assertInstanceOf(ListLinkedNotesCommand.class, command);
    }

    @Test
    void testParseListOutgoingLinksWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-outgoing-links"));
    }

    @Test
    void testParseListOutgoingLinksWithTooManyArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-outgoing-links abcd1234 extra"));
    }

    @Test
    void testParseListOutgoingLinksWithInvalidIdThrowsInvalidFormatException() {
        // Too short should fail
        assertThrows(InvalidFormatException.class, () -> Parser.parse("list-outgoing-links abc"));
    }
    //@@author
    @Test
    void testParseRenameTagValid() throws ZettelException {
        Command command = Parser.parse("rename-tag oldTag newTag");
        assertInstanceOf(RenameTagCommand.class, command);
    }

    @Test
    void testParseRenameTagMissingArgumentsThrowsException() {
        ZettelException ex = assertThrows(ZettelException.class, () -> Parser.parse("rename-tag oldTag"));
        assertTrue(ex.getMessage().contains("format"));
    }

    @Test
    void testParseRenameTagTooManyArgumentsThrowsException() {
        ZettelException ex = assertThrows(ZettelException.class, () -> Parser.parse("rename-tag oldTag newTag extra"));
        assertTrue(ex.getMessage().contains("format"));
    }

    @Test
    void testParseRenameTagWhitespaceTagsThrowsException() {
        ZettelException ex = assertThrows(ZettelException.class, () -> Parser.parse("rename-tag   "));
        assertTrue(ex.getMessage().contains("format"));
    }

    // ==================== Help Command Tests ====================

    @Test
    void testParseHelpCommandReturnsHelpCommand() throws ZettelException {
        Command command = Parser.parse("help");
        assertInstanceOf(HelpCommand.class, command);
    }

    @Test
    void testParseHelpWithArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("help me"));
    }



    // ==================== Print Body Command Tests ====================

    @Test
    void testParsePrintBodyWithValidIdReturnsPrintNoteBodyCommand() throws ZettelException {
        Command command = Parser.parse("print-body abcd1234");
        assertInstanceOf(PrintNoteBodyCommand.class, command);
    }

    @Test
    void testParsePrintBodyWithoutArgumentsThrowsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> Parser.parse("print-body"));
    }

    @Test
    void testParsePrintBodyWithInvalidIdThrowsInvalidFormatException() {
        // uppercase hex should fail
        assertThrows(InvalidFormatException.class, () -> Parser.parse("print-body ABCD1234"));
        // too short should fail
        assertThrows(InvalidFormatException.class, () -> Parser.parse("print-body abc"));
    }
}
