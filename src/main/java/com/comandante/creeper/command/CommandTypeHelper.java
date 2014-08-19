package com.comandante.creeper.command;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import java.util.Iterator;

public class CommandTypeHelper {
    public static CommandType getCommandType(CommandType[] commandTypes, String message) {
        final String rootCommand = message.split(" ")[0];
        for (CommandType commandType : commandTypes) {
            Iterator<String> iterator = commandType.getValidCommandTriggers().iterator();
            String searchString = null;
            if (!commandType.isCaseSensitive()) {
                iterator = Iterators.transform(iterator, new UpperCaseFunction<String, String>());
                searchString = rootCommand.toUpperCase();
            } else {
                searchString = rootCommand;
            }
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.equals(searchString)) {
                    return commandType;
                }
            }
        }
        return DefaultCommandType.UNKNOWN;
    }

    static class UpperCaseFunction<F, T> implements Function<F, T> {
        @Override
        public Object apply(Object f) {
            return f.toString().toUpperCase();
        }
    }

}
