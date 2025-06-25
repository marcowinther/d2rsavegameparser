/*
   Copyright 2023 Paladijn (paladijn2960+d2rsavegameparser@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package io.github.paladijn.d2rsavegameparser.parser;

import io.github.paladijn.d2rsavegameparser.model.D2Character;
import io.github.paladijn.d2rsavegameparser.model.Difficulty;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class SocketRewards {
    private static final Logger log = getLogger(SocketRewards.class);

    /**
     * This will parse a savegame folder for all .d2s files and list which ones still have their socket quest award available.
     * @param saveGameLocation the Diablo II resurrected savegame folder
     * @return a list of all character files that have finished the socket quest, but not claimed the reward yet.
     */
    public String call(final String saveGameLocation) {
        final StringBuilder filesWithSocketAvailable = new StringBuilder("These savegames have the socket quest available:");

        try (Stream<Path> pathStream = Files.list(Path.of(saveGameLocation))) {
            final List<Path> savegames = pathStream
                    .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".d2s"))
                    .toList();

            log.info("{} savegame files found", savegames.size());

            for (Path savegame: savegames) {
                final ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(savegame));

                SampleHelpers.getCharacter(byteBuffer, savegame) //TODO this line reads the file and parses it into a D2Character object
                        .ifPresent(d2Character -> checkCharacterForSocketReward(d2Character, savegame.getFileName(), filesWithSocketAvailable));
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred reading savegame files", e);
        }
        return filesWithSocketAvailable.toString();
    }

    private void checkCharacterForSocketReward(final D2Character d2Character, final Path filename, final StringBuilder filesWithSocketAvailable) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (d2Character.questDataPerDifficulty().get(difficulty.ordinal()).socketQuestRewardAvailable()) {
                filesWithSocketAvailable
                        .append("\n")
                        .append(filename)
                        .append(" [").append(difficulty).append("]");
            }
        }
    }
}
