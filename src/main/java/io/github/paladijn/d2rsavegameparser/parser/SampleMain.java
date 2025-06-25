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

//TODO example from the example project, this shows how to parse entire folder
public class SampleMain {

    public static void ParseFolder(String[] args) {
        if (args.length < 2) {
            System.err.println("Insufficient parameters\nUsage: Main <type> <saveGameLocation> [other options]");
            System.exit(1);
        }

        var socketOrSets = "sockets";

        socketOrSets = args[1].toLowerCase();

        final long start = System.currentTimeMillis();
        final String result = switch (socketOrSets) {
            case "sockets" -> {
                final SocketRewards socketRewards = new SocketRewards();
                yield socketRewards.call(args[1]);
            }
            case "sets" -> {
                final ListSets listSets = new ListSets();
                yield listSets.call(args[1], args[2]);
            }
            default -> throw new IllegalArgumentException("Unknown type: " + socketOrSets);
        };

        System.out.println("processing took " + (System.currentTimeMillis() - start));

        System.out.println(result);
    }
}
