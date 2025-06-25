package io.github.paladijn.d2rsavegameparser.parser;

import io.github.paladijn.d2rsavegameparser.model.D2Character;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;

public interface SampleHelpers {

    static Optional<D2Character> getCharacter(final ByteBuffer byteBuffer, final Path savegame) {
        CharacterParser characterParser = new CharacterParser(false);
        try {
            return Optional.of(characterParser.parse(byteBuffer));
        } catch (ParseException pe) {
            System.err.println("Could not parse path: " + savegame + ", " + pe.getMessage());
        }
        return Optional.empty();
    }
}