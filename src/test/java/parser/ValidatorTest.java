package parser;

import org.junit.jupiter.api.Test;
import seedu.zettel.exceptions.EmptyDescriptionException;
import seedu.zettel.exceptions.ForbiddenCharacterFoundException;
import seedu.zettel.exceptions.InvalidFormatException;
import seedu.zettel.exceptions.LengthExceedException;
import seedu.zettel.exceptions.ZettelException;
import seedu.zettel.parser.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test class for Validator utility methods.
 */
class ValidatorTest {

    // ==================== Note ID Validation Tests ====================

    @Test
    void validateNoteId_validId_returnsId() throws ZettelException {
        String validId = "a1b2c3d4";
        String result = Validator.validateNoteId(validId, "test");
        assertEquals("a1b2c3d4", result);
    }

    @Test
    void validateNoteId_validIdWithSpaces_returnsTrimmedId() throws ZettelException {
        String validId = "  a1b2c3d4  ";
        String result = Validator.validateNoteId(validId, "test");
        assertEquals("a1b2c3d4", result);
    }

    @Test
    void validateNoteId_nullId_throwsEmptyDescriptionException() {
        Exception exception = assertThrows(EmptyDescriptionException.class, () -> {
            Validator.validateNoteId(null, "test");
        });
        assertTrue(exception.getMessage().contains("Please specify a Note ID to test!"));
    }

    @Test
    void validateNoteId_emptyId_throwsEmptyDescriptionException() {
        Exception exception = assertThrows(EmptyDescriptionException.class, () -> {
            Validator.validateNoteId("", "test");
        });
        assertTrue(exception.getMessage().contains("Please specify a Note ID to test!"));
    }

    @Test
    void validateNoteId_whitespaceOnlyId_throwsEmptyDescriptionException() {
        Exception exception = assertThrows(EmptyDescriptionException.class, () -> {
            Validator.validateNoteId("   ", "test");
        });
        assertTrue(exception.getMessage().contains("Please specify a Note ID to test!"));
    }

    @Test
    void validateNoteId_tooShortId_throwsInvalidFormatException() {
        Exception exception = assertThrows(InvalidFormatException.class, () -> {
            Validator.validateNoteId("a1b2c3", "test");
        });
        assertTrue(exception.getMessage().contains("Note ID must be exactly 8 hexadecimal characters"));
    }

    @Test
    void validateNoteId_tooLongId_throwsInvalidFormatException() {
        Exception exception = assertThrows(InvalidFormatException.class, () -> {
            Validator.validateNoteId("a1b2c3d4e5", "test");
        });
        assertTrue(exception.getMessage().contains("Note ID must be exactly 8 hexadecimal characters"));
    }

    @Test
    void validateNoteId_uppercaseHexId_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateNoteId("A1B2C3D4", "test");
        });
    }

    @Test
    void validateNoteId_invalidCharacters_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateNoteId("g1h2i3j4", "test");
        });
    }

    @Test
    void validateNoteId_specialCharacters_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateNoteId("a1b2-3d4", "test");
        });
    }

    // ==================== Command Input Validation Tests ====================

    @Test
    void validateCommandInput_nullInput_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateCommandInput(null));
    }

    @Test
    void validateCommandInput_validAsciiInput_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateCommandInput("new -t Test Title -b Test Body"));
    }

    @Test
    void validateCommandInput_withPipeCharacter_throwsForbiddenCharacterFoundException() {
        Exception exception = assertThrows(ForbiddenCharacterFoundException.class, () -> {
            Validator.validateCommandInput("new -t Test|Title");
        });
        assertTrue(exception.getMessage().contains("Invalid character '|' detected"));
    }

    @Test
    void validateCommandInput_withEmoji_throwsForbiddenCharacterFoundException() {
        Exception exception = assertThrows(ForbiddenCharacterFoundException.class, () -> {
            Validator.validateCommandInput("new -t Test 😀 Title");
        });
        assertTrue(exception.getMessage().contains("Only ASCII characters are allowed"));
    }

    @Test
    void validateCommandInput_withGreekLetters_throwsForbiddenCharacterFoundException() {
        Exception exception = assertThrows(ForbiddenCharacterFoundException.class, () -> {
            Validator.validateCommandInput("new -t Test α β γ");
        });
        assertTrue(exception.getMessage().contains("Only ASCII characters are allowed"));
    }

    @Test
    void validateCommandInput_withChineseCharacters_throwsForbiddenCharacterFoundException() {
        Exception exception = assertThrows(ForbiddenCharacterFoundException.class, () -> {
            Validator.validateCommandInput("new -t 测试");
        });
        assertTrue(exception.getMessage().contains("Only ASCII characters are allowed"));
    }

    @Test
    void validateCommandInput_withAccentedCharacters_throwsForbiddenCharacterFoundException() {
        Exception exception = assertThrows(ForbiddenCharacterFoundException.class, () -> {
            Validator.validateCommandInput("new -t Café");
        });
        assertTrue(exception.getMessage().contains("Only ASCII characters are allowed"));
    }

    @Test
    void validateCommandInput_exceedsMaxLength_throwsLengthExceedException() {
        String longInput = "a".repeat(3001);
        Exception exception = assertThrows(LengthExceedException.class, () -> {
            Validator.validateCommandInput(longInput);
        });
        assertTrue(exception.getMessage().contains("must be less than 3000 characters"));
    }

    @Test
    void validateCommandInput_exactlyMaxLength_doesNotThrow() {
        String exactMaxInput = "a".repeat(3000);
        assertDoesNotThrow(() -> Validator.validateCommandInput(exactMaxInput));
    }

    // ==================== Title/Tag Validation Tests ====================

    @Test
    void validateTitleTag_nullInput_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateTitleTag(null, "Title"));
    }

    @Test
    void validateTitleTag_validAlphanumeric_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateTitleTag("Test Title 123", "Title"));
    }

    @Test
    void validateTitleTag_onlyLetters_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateTitleTag("TestTitle", "Title"));
    }

    @Test
    void validateTitleTag_onlyNumbers_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateTitleTag("12345", "Title"));
    }

    @Test
    void validateTitleTag_withSpaces_doesNotThrow() {
        assertDoesNotThrow(() -> Validator.validateTitleTag("Test   Title   123", "Title"));
    }

    @Test
    void validateTitleTag_exceedsMaxLength_throwsLengthExceedException() {
        String longInput = "a".repeat(201);
        Exception exception = assertThrows(LengthExceedException.class, () -> {
            Validator.validateTitleTag(longInput, "Title");
        });
        assertTrue(exception.getMessage().contains("Title must be less than 200 characters"));
    }

    @Test
    void validateTitleTag_exactlyMaxLength_doesNotThrow() {
        String exactMaxInput = "a".repeat(200);
        assertDoesNotThrow(() -> Validator.validateTitleTag(exactMaxInput, "Title"));
    }

    @Test
    void validateTitleTag_withSpecialCharacters_throwsInvalidFormatException() {
        Exception exception = assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test-Title", "Title");
        });
        assertTrue(exception.getMessage().contains("Title contains invalid characters"));
    }

    @Test
    void validateTitleTag_withUnderscores_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test_Title", "Title");
        });
    }

    @Test
    void validateTitleTag_withPunctuation_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test! Title?", "Title");
        });
    }

    @Test
    void validateTitleTag_withDots_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test.Title", "Title");
        });
    }

    @Test
    void validateTitleTag_withCommas_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test, Title", "Title");
        });
    }

    @Test
    void validateTitleTag_withEmoji_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test 😀 Title", "Title");
        });
    }

    @Test
    void validateTitleTag_withPipe_throwsInvalidFormatException() {
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("Test|Title", "Title");
        });
    }

    @Test
    void validateTitleTag_tagActionName_includesCorrectName() {
        Exception exception = assertThrows(LengthExceedException.class, () -> {
            Validator.validateTitleTag("a".repeat(201), "Tag");
        });
        assertTrue(exception.getMessage().contains("Tag must be less than 200 characters"));
    }

    @Test
    void validateTitleTag_emptyString_doesNotThrowButAllowsEmpty() {
        // Based on the implementation, empty strings don't explicitly fail length check
        // but will fail the regex check since empty doesn't match ^[a-zA-Z0-9 ]+$
        assertThrows(InvalidFormatException.class, () -> {
            Validator.validateTitleTag("", "Title");
        });
    }

    // ==================== Edge Cases ====================

    @Test
    void validateNoteId_allZeros_returnsValid() throws ZettelException {
        String result = Validator.validateNoteId("00000000", "test");
        assertEquals("00000000", result);
    }

    @Test
    void validateNoteId_allNines_returnsValid() throws ZettelException {
        String result = Validator.validateNoteId("99999999", "test");
        assertEquals("99999999", result);
    }

    @Test
    void validateNoteId_allLowercaseF_returnsValid() throws ZettelException {
        String result = Validator.validateNoteId("ffffffff", "test");
        assertEquals("ffffffff", result);
    }

    @Test
    void validateCommandInput_onlyAsciiSpecialChars_doesNotThrow() {
        assertDoesNotThrow(() ->
                Validator.validateCommandInput("!@#$%^&*()_+-=[]{}\\;':\",./<>?")
        );
    }

    @Test
    void validateTitleTag_mixedCaseAlphanumeric_doesNotThrow() {
        assertDoesNotThrow(() ->
                Validator.validateTitleTag("TeSt TiTlE 123 ABC xyz", "Title")
        );
    }

    @Test
    void validateCommandInput_exactlyAtBoundary3000_doesNotThrow() {
        String boundaryInput = "x".repeat(2999) + "y";
        assertDoesNotThrow(() -> Validator.validateCommandInput(boundaryInput));
    }

    @Test
    void validateTitleTag_exactlyAtBoundary200_doesNotThrow() {
        String boundaryInput = "x".repeat(199) + "y";
        assertDoesNotThrow(() -> Validator.validateTitleTag(boundaryInput, "Title"));
    }
}
