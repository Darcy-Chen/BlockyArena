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

import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.games.MatchRules;
import io.github.mrdarcychen.games.PlayerAssistant;
import io.github.mrdarcychen.utils.DamageData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.List;

public abstract class MatchState {

    protected final GameSession gameSession;
    protected List<Player> players;
    protected MatchRules matchRules;
    protected PlayerAssistant playerAssistant;

    public MatchState(GameSession gameSession, List<Player> players) {
        this.gameSession = gameSession;
        this.players = players;
        matchRules = gameSession.getTeamMode();
        this.playerAssistant = gameSession.getPlayerAssistant();
    }

    public void recruit(Player player) {
        players.add(player);
        playerAssistant.recruit(player);
    }

    public void dismiss(Player player) {
        playerAssistant.dismiss(player);
    }

    public void eliminate(Player player, Text cause) {
        broadcast(cause);
        playerAssistant.eliminate(player);
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
            damageData.getVictim().setTransform(gameSession.getArena().getLobbySpawn().getTransform());
        }
        event.setCancelled(true);
    }

    /**
     * Broadcasts the given message to all connected Gamers in this Game.
     *
     * @param msg the message to be delivered
     */
    public void broadcast(Text msg) {
        players.forEach(it -> it.sendMessage(msg));
    }
}
