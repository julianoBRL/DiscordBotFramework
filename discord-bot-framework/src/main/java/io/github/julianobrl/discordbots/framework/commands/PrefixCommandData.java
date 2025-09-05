package io.github.julianobrl.discordbots.framework.commands;

import io.github.julianobrl.discordbots.framework.annotations.commands.PrefixCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrefixCommandData {

    private IExecuteCommands command;
    private PrefixCommand info;
    private Class<? extends IExecuteCommands> baseClass;

}
