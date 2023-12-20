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
import java.util.HashSet;
import java.util.Set;

public class EShareCommand extends Command {

    private final EShareAddon addon;
    private final Set<String> uploads = new HashSet<>();

    public EShareCommand(EShareAddon addon) {
        super("eshare");
        this.addon = addon;
    }

    @Override
    public boolean execute(String prefix, String[] args) {
        if(addon.configuration().token().isBlank()) {
            displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.errors.noToken", NamedTextColor.RED)));
            return true;
        }
        if(args.length < 1) {
            displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.errors.file", NamedTextColor.RED)));
            return true;
        }
        File file = new File(System.getProperty("user.dir") + "/screenshots/" + args[0]);
        if(!file.exists()) {
            displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.errors.file", NamedTextColor.RED)));
            return true;
        }
        if(uploads.contains(file.getName()) && addon.configuration().doubleUploads()) {
            if(args.length < 2 || !args[1].equalsIgnoreCase("force")) {
                displayMessage(EShareAddon.prefix.copy().append(
                    Component.translatable(
                        "eshare.errors.alreadyUploaded",
                        NamedTextColor.RED,
                        Component.translatable(
                            "eshare.messages.uploadAnyway",
                            Style.empty()
                                .color(NamedTextColor.AQUA)
                                .decorate(TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.showText(Component.translatable("eshare.upload.hover", NamedTextColor.GREEN)))
                                .clickEvent(ClickEvent.runCommand(String.format(
                                    "/%s %s force",
                                    prefix,
                                    file.getName()
                                )))
                        )
                    ))
                );
                return true;
            }
        }
        uploads.add(file.getName());
        displayMessage(EShareAddon.prefix.copy().append(Component.translatable("eshare.messages.uploading", NamedTextColor.GRAY)));
        UploadRequest request = new UploadRequest(file, addon.configuration().token());
        request.sendAsyncRequest().thenAccept((response) -> {
            if(request.isSuccessful()) {
                Component copy = Component.translatable(
                    "eshare.upload.copy",
                    Style.empty()
                        .color(NamedTextColor.AQUA)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(
                            HoverEvent.showText(Component.translatable("eshare.upload.hover").color(NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.copyToClipboard(request.getUploadLink()))
                );
                Component open = Component.translatable(
                    "eshare.upload.open",
                    Style.empty()
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(
                            HoverEvent.showText(Component.translatable("eshare.upload.hover").color(NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl(request.getUploadLink()))
                );
                Component component = Component.translatable(
                    "eshare.messages.uploaded",
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
