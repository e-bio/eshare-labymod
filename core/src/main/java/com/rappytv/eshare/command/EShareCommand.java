package com.rappytv.eshare.command;

import com.rappytv.eshare.EShareAddon;
import com.rappytv.eshare.api.UploadRequest;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import java.io.File;

public class EShareCommand extends Command {

    private final EShareAddon addon;

    public EShareCommand(EShareAddon addon) {
        super("eshare");
        this.addon = addon;
    }

    @Override
    public boolean execute(String prefix, String[] args) {
        if(addon.configuration().token().isBlank()) {
            displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.messages.noToken", NamedTextColor.RED)));
            return true;
        }
        if(args.length < 1) {
            displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.messages.file", NamedTextColor.RED)));
            return true;
        }
        File file = new File("%appdata%/screenshots/" + args[0]);
        if(!file.exists()) {
            displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.messages.file", NamedTextColor.RED)));
            return true;
        }
        displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.messages.uploading", NamedTextColor.RED)));
        UploadRequest request = new UploadRequest(file, addon.configuration().token());
        request.sendAsyncRequest().thenAccept((response) -> {
            if(request.isSuccessful()) {
                Component copy = Component.translatable(
                    "eshare.upload.copy",
                    Style.builder()
                        .color(NamedTextColor.AQUA)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(
                            HoverEvent.showText(Component.translatable("eshare.upload.hover").color(NamedTextColor.GREEN))).build()
                        .clickEvent(ClickEvent.copyToClipboard(request.getUploadLink()))
                );
                Component open = Component.translatable(
                    "eshare.upload.open",
                    Style.builder()
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(
                            HoverEvent.showText(Component.translatable("eshare.upload.hover").color(NamedTextColor.GREEN))).build()
                        .clickEvent(ClickEvent.openUrl(request.getUploadLink()))
                );
                Component component = Component.translatable(
                    "eshare.upload.uploaded",
                    !request.getUploadLink().isBlank() ? copy : Component.text(""),
                    !request.getUploadLink().isBlank() ? open : Component.text("")
                ).color(NamedTextColor.GRAY);

                Laby.references().chatExecutor().displayClientMessage(EShareAddon.prefix.copy().append(component));
            } else {
                Laby.references().chatExecutor().displayClientMessage(
                    EShareAddon.prefix.copy().append(Component.text(
                        request.getError(),
                        NamedTextColor.RED
                    ))
                );
            }
        }).exceptionally((e) -> {
            Laby.references().chatExecutor().displayClientMessage(
                EShareAddon.prefix.copy().append(Component.text(
                    e.getMessage(),
                    NamedTextColor.RED
                ))
            );
            return null;
        });
        return true;
    }
}
