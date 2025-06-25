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
import io.github.paladijn.d2rsavegameparser.model.Item;
import io.github.paladijn.d2rsavegameparser.model.ItemQuality;
import io.github.paladijn.d2rsavegameparser.model.SharedStashTab;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class ListUniques {

    /**
     * This will parse a savegame folder for all .d2s files along with the shared stash and list all the set items in them. It will also translate the item names.
     * @param saveGameLocation the Diablo II resurrected savegame folder
     * @param language the language to use, supported values: enUS, deDE, esES, esMX, frFR, itIT, jaJP, koKR, plPL, ptBR, ruRU, zhCN, zhTW
     * @return a list of set items, with their translated name
     */
    public String call(final String saveGameLocation, final String language) {
        List<SavegameWithItem> setItems = new ArrayList<>();

        // we now filter on softCore, if you want to filter hardcore only adjust the two lines below to true and HARDCORE_SHARED_STASH
        filterSetItemsInCharacterFiles(saveGameLocation, setItems, false);
        filterSetItemsInSharedStash(saveGameLocation, SharedStashParser.SOFTCORE_SHARED_STASH, setItems);

        System.out.println(setItems.size() + " unique items found");
        setItems.sort(new SetItemSortedById());


        StringBuilder result = new StringBuilder("unique items found");
        setItems.forEach(setItem ->
            result.append("\n")
                    .append(setItem.item().itemName())
                    .append(" -> ").append(setItem.savegame())
                    .append(" at ").append(setItem.item().location()).append(" (").append(setItem.item().container())
                    .append(") [").append(setItem.item().x()).append(", ").append(setItem.item().y()).append("]")
        );
        return result.toString();
    }

    private static void filterSetItemsInCharacterFiles(final String saveGameLocation, final List<SavegameWithItem> setItems, final boolean filterHardcore) {
        try (Stream<Path> pathStream = Files.list(Path.of(saveGameLocation))) {
            final List<Path> savegames = pathStream
                    .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".d2s"))
                    .toList();

            System.out.println(savegames.size()+ " savegame files found");

            for (Path savegame: savegames) {
                final ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(savegame));

                SampleHelpers.getCharacter(byteBuffer, savegame)
                        .ifPresent(d2Character -> {
                            if (d2Character.hardcore() == filterHardcore) {
                                filterSetItems(savegame.getFileName(), d2Character, setItems);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred reading savegame files", e);
        }
    }

    private static void filterSetItemsInSharedStash(final String saveGameLocation, final String stashLocation,  final List<SavegameWithItem> setItems) {
        final SharedStashParser sharedStashParser = new SharedStashParser(false);
        final Path sharedStash = Path.of(saveGameLocation, stashLocation);

        final ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Files.readAllBytes(sharedStash));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final List<SharedStashTab> sharedStashTabs = sharedStashParser.parse(byteBuffer);
        sharedStashTabs.forEach(sharedStashTab -> sharedStashTab.items().stream()
                .filter(item -> item.quality() == ItemQuality.UNIQUE)
                .forEach(item -> setItems.add(new SavegameWithItem(stashLocation, item)))
        );
    }

    private static void filterSetItems(Path savegame, D2Character d2Character, List<SavegameWithItem> setItems) {
        d2Character.items().stream()
                .filter(item -> item.quality() == ItemQuality.UNIQUE)
                .forEach(item -> setItems.add(new SavegameWithItem(savegame.toString(), item)));

        // mercenaries can have unique items too!
        Optional.ofNullable(d2Character.mercenary())
                .ifPresent(mercenary -> mercenary.items().stream()
                    .filter(item -> item.quality() == ItemQuality.UNIQUE)
                    .forEach(item -> setItems.add(new SavegameWithItem(savegame.toString(), item))));
    }

}

record SavegameWithItem(String savegame, Item item) {}

class UniqueItemSortedById implements Comparator<SavegameWithItem> {
    @Override
    public int compare(SavegameWithItem o1, SavegameWithItem o2) {
        return Short.compare(o1.item().setItemId(), o2.item().setItemId());
    }
}