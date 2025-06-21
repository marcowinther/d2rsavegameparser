package io.github.paladijn.d2rsavegameparser.parser;

import io.github.paladijn.d2rsavegameparser.model.D2Character;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class D2CharacterLoader {

    /**
     * Reads the file at the given path into a ByteBuffer.
     * @param filePath the path to the .d2s file
     * @return ByteBuffer containing the file's contents
     * @throws IOException if the file cannot be read
     */
    public static ByteBuffer loadFileToByteBuffer(String filePath) throws IOException {
        try (FileChannel fileChannel = FileChannel.open(Path.of(filePath), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(buffer);
            buffer.flip();
            return buffer;
        }
    }

    /**
     * Parses a ByteBuffer into a D2Character using CharacterParser.
     * @param buffer the ByteBuffer containing the save file data
     * @return the parsed D2Character
     */
    public static D2Character parseCharacter(ByteBuffer buffer) {
        CharacterParser parser = new CharacterParser(false);
        return parser.parse(buffer);
    }

    /**
     * Loads a save file and returns whether the character is an expansion character.
     * @param filePath the path to the .d2s file
     * @return true if expansion, false otherwise
     * @throws Exception if the file cannot be read or parsed
     */
    public static boolean isExpansionCharacter(String filePath) throws Exception {
        ByteBuffer buffer = loadFileToByteBuffer(filePath);
        D2Character character = parseCharacter(buffer);
        return character.expansion();
    }

    
}
