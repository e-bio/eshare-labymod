package com.rappytv.eshare.listener;

import com.rappytv.eshare.EShareAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.misc.WriteScreenshotEvent;

public class ScreenshotListener {

    @Subscribe
    public void onScreenshot(WriteScreenshotEvent event) {
        Component component = EShareAddon.prefix.copy().append(
            Component.translatable(
                "eshare.messages.upload",
                Style.empty()
                    .color(NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/eshare " + event.getDestination().getName()))
            )
        );

        Laby.references().chatExecutor().displayClientMessage(component);
    }
}
