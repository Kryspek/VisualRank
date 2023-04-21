package com.ts.visualranks.command.argument;

import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.visualrank.VisualRank;
import com.ts.visualranks.visualrank.VisualRankRepository;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;
import java.util.List;

@ArgumentName("rank")
public class VisualRankArgument implements OneArgument<VisualRank> {

    private final VisualRankRepository visualRankRepository;
    private final MessageConfiguration messageConfiguration;

    public VisualRankArgument(VisualRankRepository visualRankRepository, MessageConfiguration messageConfiguration) {
        this.visualRankRepository = visualRankRepository;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public Result<VisualRank, ?> parse(LiteInvocation invocation, String argument) {
        VisualRank visualRank = this.visualRankRepository.getVisualRank(argument);

        if (visualRank == null) {
            return Result.error(this.messageConfiguration.wrongUsage.noRank);
        }

        return Result.ok(visualRank);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return this.visualRankRepository.getVisualRanks()
                .stream()
                .map(VisualRank::getName)
                .map(Suggestion::of)
                .toList();
    }
}
