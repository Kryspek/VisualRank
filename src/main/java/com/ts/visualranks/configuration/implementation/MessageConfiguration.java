package com.ts.visualranks.configuration.implementation;

import com.ts.visualranks.configuration.ReloadableConfig;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;

public class MessageConfiguration implements ReloadableConfig {

    @Description({ " ", "# Wrong command usage" })
    public WrongUsage wrongUsage = new WrongUsage();

    @Description({ " ", "# Visual rank section" })
    public VisualRankSection visualRankSection = new VisualRankSection();

    @Contextual
    public static class WrongUsage {
        public String invalidUsage = "&4Wrong command usage &8>> &7{COMMAND}.";

        public String invalidUsageHeader = "&cWrong command usage!";

        public String invalidUsageEntry = "&8 >> &7";

        public String noPermission = "&4You don't have permission to perform this command.";

        public String cantFindPlayer = "&4Can not find that player!";

        public String onlyForPlayer = "&4Command only for players!";

        public String noRank = "&4This rank doesn't exist!";
    }

    @Contextual
    public static class VisualRankSection {
        public String purchased = "&aYou have purchased &7{RANK} &arank for &7{PRICE}&a!";

        public String alreadyEquipped = "&4You already have this rank equipped!";

        public String equipRank = "&aYou have equipped &7{RANK} &arank!";

        public String notEnoughMoney = "&4You don't have enough money to purchase this rank!";

        public String resetVisualRank = "&aYou have reset your visual rank!";

        public String reloadConfiguration = "&aYou have reloaded configuration!";

        public String addVisualRank = "&aYou have added &7{RANK} &arank for &7{PLAYER}&a!";

        public String recivedVisualRank = "&aYou have recived &7{RANK} &arank!";

    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "messages.yml");
    }

}