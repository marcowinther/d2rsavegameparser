package io.github.paladijn.d2rsavegameparser.parser;

import io.github.paladijn.d2rsavegameparser.model.D2Character;
import io.github.paladijn.d2rsavegameparser.util.SimpleJsonBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class D2CharacterLoader {

    /**
     * Reads the file at the given path into a ByteBuffer.
     * 
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
     * 
     * @param buffer the ByteBuffer containing the save file data
     * @return the parsed D2Character
     */
    public static D2Character parseCharacter(ByteBuffer buffer) {
        CharacterParser parser = new CharacterParser(false);
        return parser.parse(buffer);
    }

    /**
     * Loads a save file and returns whether the character is an expansion
     * character.
     * 
     * @param filePath the path to the .d2s file
     * @return true if expansion, false otherwise
     * @throws Exception if the file cannot be read or parsed
     */
    public static boolean isExpansionCharacter(String filePath) throws Exception {
        ByteBuffer buffer = loadFileToByteBuffer(filePath);
        D2Character character = parseCharacter(buffer);
        return character.expansion();
    }

    /**
     * Creates a JSON representation of the given D2Character.
     * 
     * @param character the D2Character to convert
     * @return JSON string
     */
    public static String CreateCharacterJson(D2Character character) {
        SimpleJsonBuilder json = new SimpleJsonBuilder();
        json.beginObject()
                .beginArray("characters")
                .beginObject()
                .addField("name", character.name())
                .addField("level", character.level())
                .addRawField("socketQuestAvailable",
                        new SimpleJsonBuilder().beginObject()
                                .addField("normal",
                                        character.questDataPerDifficulty().get(0).socketQuestRewardAvailable())
                                .addField("nightmare",
                                        character.questDataPerDifficulty().get(1).socketQuestRewardAvailable())
                                .addField("hell",
                                        character.questDataPerDifficulty().get(2).socketQuestRewardAvailable())
                                .endObject().toString())
                .beginArray("items");
        if (character.items() != null) {
            boolean first = true;
            for (var item : character.items()) {
                if (!first) json.addRawValue(""); // triggers comma in builder
                json.beginObject()
                        .addField("id", item.uniqueId())
                        .addField("name", item.itemName())
                        .addField("type", item.type())
                        .addField("sockets", item.cntSockets())
                    .endObject();
                first = false;
            }
        }
        json.endArray()
                .endObject()
                .endArray()
                .endObject();
        return json.toString();
    }

    /**
     * Loads a save file and returns its JSON representation using
     * CreateCharacterJson.
     * 
     * @param filePath the path to the .d2s file
     * @return JSON string
     * @throws Exception if the file cannot be read or parsed
     */
    public static String createCharacterJsonFromFile(String filePath) throws Exception {
        ByteBuffer buffer = loadFileToByteBuffer(filePath);
        D2Character character = parseCharacter(buffer);
        return CreateCharacterJson(character);
    }
}
