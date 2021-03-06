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

package io.github.mrdarcychen.commands;

import io.github.mrdarcychen.ConfigManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.string;

public class CmdCreate implements CommandExecutor {

    public static final CommandSpec SPEC = CommandSpec.builder()
            .arguments(
                    onlyOne(string(Text.of("type"))),
                    onlyOne(string(Text.of("id"))))
            .executor(new CmdCreate())
            .permission("blockyarena.create")
            .build();

    private CmdCreate() {
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("This command must be executed by a player."));
            return CommandResult.empty();
        }
        Player player = (Player) src;
        String type = args.<String>getOne("type").get();
        String id = args.<String>getOne("id").get();

        switch (type) {
            case "arena":
                CmdEdit.expectBuilder(player, id);
                player.sendMessage(Text.of("Start configuring interactively with /ba edit " + id +
                        " spawn <type>."));
                player.sendMessage(Text.of("For detailed instructions, please visit the GitHub" +
                        " repo."));
                player.sendMessage(Text.of("Execute /ba edit " + id + " save when you're done."));
                player.sendMessage(Text.of("Execute /ba create arena " + id + " to start over."));
                return CommandResult.success();
            case "reward":
                ConfigManager.getInstance().getConfNode("reward").setValue(id);
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "kit onetime " + id + " true");
                player.sendMessage(Text.of("Reward has been set to " + id + ". Winners must have the permission \n" +
                        "nucleus.kits." + id + " in order to receive this reward."));
                return CommandResult.success();
            default:
                player.sendMessage(Text.of("Invalid argument <type>. Must be arena."));
        }
        return CommandResult.empty();
    }
}
