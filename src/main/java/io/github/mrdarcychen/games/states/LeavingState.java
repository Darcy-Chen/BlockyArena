/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.commands.CmdJoin;
import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.games.PlayerManager;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

public class LeavingState extends MatchState {

    public LeavingState(GameSession gameSession, List<Player> players) {
        super(gameSession, players);

        gameSession.getPlayerAssistant().dismissAll();

        playerAssistant.dismissAll();
        players.forEach(it -> {
            // TODO: set player status as inactive
            PlayerManager.clearGame(it.getUniqueId());
            playerAssistant.setSpectatorProperties(it, false);
        });
        CmdJoin.remove(gameSession);
    }
}