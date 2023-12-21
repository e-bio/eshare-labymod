package com.rappytv.eshare;

import com.rappytv.eshare.command.EShareCommand;
import com.rappytv.eshare.listener.ScreenshotListener;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.notification.Notification;

@AddonMain
public class EShareAddon extends LabyAddon<EShareConfig> {


    public static final Component prefix = Component.empty()
        .append(Component.text("ESHARE", Style.empty().color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)))
        .append(Component.text(" » ", NamedTextColor.DARK_GRAY));
    private static Icon icon;

    @Override
    protected void enable() {
        registerSettingCategory();
        registerCommand(new EShareCommand(this));
        registerListener(new ScreenshotListener());

        icon = Icon.texture(ResourceLocation.create("eshare", "textures/notification.png"));
    }

    @Override
    protected Class<? extends EShareConfig> configurationClass() {
        return EShareConfig.class;
    }

    public static void notification(Component title, Component description) {
        Laby.references().notificationController().push(
            Notification.builder()
                .title(title)
                .text(description)
                .icon(icon)
                .build()
        );
    }
}
