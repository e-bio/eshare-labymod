package com.rappytv.eshare;

import com.rappytv.eshare.command.EShareCommand;
import com.rappytv.eshare.listener.ScreenshotListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class EShareAddon extends LabyAddon<EShareConfig> {

    public static final Component prefix = Component.empty()
        .append(Component.text("ESHARE", Style.empty().color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)))
        .append(Component.text(" » ", NamedTextColor.DARK_GRAY));

    @Override
    protected void enable() {
        registerSettingCategory();
        registerCommand(new EShareCommand(this));
        registerListener(new ScreenshotListener());
    }

    @Override
    protected Class<? extends EShareConfig> configurationClass() {
        return EShareConfig.class;
    }
}
