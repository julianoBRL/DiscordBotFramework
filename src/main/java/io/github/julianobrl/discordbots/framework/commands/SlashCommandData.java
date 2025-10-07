package io.github.julianobrl.discordbots.framework.commands;

import io.github.julianobrl.discordbots.framework.annotations.commands.SlashCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlashCommandData {

    private IExecuteCommands command;
    private SlashCommand info;
    private Class<? extends IExecuteCommands> baseClass;


}
