package br.com.strixcloud.lstop.bukkit.command.lstop.sub;

import br.com.strixcloud.lstop.StrixLSTop;
import br.com.strixcloud.lstop.entities.Messages;
import br.com.strixcloud.lstop.entities.util.SubCommand;
import br.com.strixcloud.lstop.services.hologram.delete.HologramDeleteController;
import lombok.var;
import org.bukkit.command.CommandSender;

public class DeleteHologramSub extends SubCommand {

    public DeleteHologramSub() {
        super("deletehologram", "", "strixlstop.delete", "deletar", "delete", "remover");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var hasHologram = StrixLSTop.getInstance().getHologramProvider() != null;
        if (!hasHologram) {
            sender.sendMessage(Messages.ERROR_NOT_HOLOGRAM.getMessage());
            return;
        }

        HologramDeleteController.getInstance().handle();

        sender.sendMessage(Messages.HOLOGRAM_DELETED.getMessage());
    }
}
