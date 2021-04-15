package br.com.strixcloud.lstop.entities.data.impl;

import br.com.strixcloud.lstop.StrixLSTop;
import br.com.strixcloud.lstop.entities.data.IPlacedHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import lombok.var;
import org.bukkit.Location;

public class HDPlacedHologram implements IPlacedHologram {
    private final Hologram holo;

    public HDPlacedHologram(Location loc) {
        var pl = StrixLSTop.getInstance();
        holo = HologramsAPI.createHologram(pl, loc);
    }

    public HDPlacedHologram(Hologram hologram) {
        this.holo = hologram;
    }

    @Override
    public Location getLocation() {
        return holo.getLocation();
    }

    @Override
    public void clear() {
        holo.clearLines();
    }

    @Override
    public void append(String content) {
        holo.appendTextLine(content);
    }

    @Override
    public void add(int line, String content) {
        holo.insertTextLine(line, content);
    }

    @Override
    public void remove(int line) {
        holo.removeLine(line);
    }

    @Override
    public int size() {
        return holo.size();
    }

    @Override
    public void delete() {
        holo.delete();
    }
}
