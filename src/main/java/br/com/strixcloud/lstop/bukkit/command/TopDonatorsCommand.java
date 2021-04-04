package br.com.strixcloud.lstop.bukkit.command;

import br.com.strixcloud.lstop.data.AccountsDAO;
import br.com.strixcloud.lstop.entities.Messages;
import br.com.strixcloud.lstop.entities.data.TopAccount;
import br.com.strixcloud.lstop.entities.util.order.Orderable;
import br.com.strixcloud.lstop.entities.util.order.impl.OrderByASC;
import lombok.var;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class TopDonatorsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var toOrder = new ArrayList<Orderable>(AccountsDAO.getInstance().get());
        var accounts = OrderByASC.getInstance().order(toOrder);
        StringBuilder message = new StringBuilder(Messages.TOP_DONATOR_HEADER.getListMessage()
                .stream()
                .map(m -> m + "\n")
                .reduce("", String::concat));

        for (var obj : accounts) {
            var acc = (TopAccount) obj;
            message.append(Messages.TOP_DONATOR_VALUE.getMessage().replace("@player", acc.getPlayer()).replace("@value", String.valueOf(acc.getValue()))).append("\n");
        }

        message.append(Messages.TOP_DONATOR_FOOTER.getListMessage()
                .stream()
                .map(m -> m + "\n")
                .reduce("", String::concat));

        sender.sendMessage(message.toString());

        return false;
    }
}