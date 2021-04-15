package br.com.strixcloud.lstop.services.hologram.setup;

import br.com.strixcloud.lstop.data.AccountsDAO;
import br.com.strixcloud.lstop.entities.data.IPlacedHologram;
import br.com.strixcloud.lstop.entities.data.TopAccount;
import br.com.strixcloud.lstop.entities.util.order.Orderable;
import br.com.strixcloud.lstop.entities.util.order.impl.OrderByASC;
import br.com.strixcloud.lstop.provider.config.IDisplayProvider;
import lombok.AllArgsConstructor;
import lombok.var;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

@AllArgsConstructor
public class HologramSetupService {

    private final IDisplayProvider displayProvider;

    private final DecimalFormat decimalFormatter = new DecimalFormat(
            "#,###.##",
            new DecimalFormatSymbols(
                    new Locale("pt", "BR")
            )
    );

    public void execute(IPlacedHologram hologram) {
        hologram.clear();
        var holoData = displayProvider.getHologramData();
        holoData.getHeader().forEach(hologram::append);

        var toOrder = new ArrayList<Orderable>(AccountsDAO.getInstance().get());
        var accounts = OrderByASC.getInstance().order(toOrder);

        var holoSize = displayProvider.getHologramData().getSize();
        for (int i = 0;i < holoSize; i++) {
            var line = displayProvider.getHologramData().getContentInvalid();
            if (i < accounts.size()) {
                var acc = (TopAccount) accounts.get(i);
                if (acc != null) {
                    line = holoData.getContentValid()
                            .replace("@value", decimalFormatter.format(acc.getValue()))
                            .replace("@player", acc.getPlayer());
                }
            }

            if (line.length() > 0) hologram.append(line);
        }

        holoData.getFooter().forEach(hologram::append);
    }

}
