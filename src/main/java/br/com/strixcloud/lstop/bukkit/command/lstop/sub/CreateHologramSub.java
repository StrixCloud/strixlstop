package br.com.strixcloud.lstop.bukkit.command.lstop.sub;

import br.com.strixcloud.lstop.StrixLSTop;
import br.com.strixcloud.lstop.entities.Messages;
import br.com.strixcloud.lstop.entities.util.SubCommand;
import br.com.strixcloud.lstop.services.hologram.create.HologramCreateController;
import lombok.var;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateHologramSub extends SubCommand {

    public CreateHologramSub() {
        super("createhologram", "", "strixlstop.create", "set", "setar");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var hasHologram = StrixLSTop.getInstance().getHologramProvider() != null;
        if (!hasHologram) {
            sender.sendMessage(Messages.ERROR_NOT_HOLOGRAM.getMessage());
            return;
        }

        HologramCreateController.getInstance().handle(((Player) sender).getLocation());

        sender.sendMessage(Messages.HOLOGRAM_CREATED.getMessage());
    }

}
