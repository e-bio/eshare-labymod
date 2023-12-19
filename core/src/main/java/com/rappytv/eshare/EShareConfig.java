package com.rappytv.eshare;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labymod.api.Laby;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.notification.Notification;
import net.labymod.api.notification.NotificationController;
import net.labymod.api.util.MethodOrder;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

@SpriteTexture("settings")
public class EShareConfig extends AddonConfig {

    @SettingSection("register")
    @SpriteSlot(size = 32)
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> doubleUploads = new ConfigProperty<>(true);


    @SettingSection("token")
    @MethodOrder(after = "doubleUploads")
    @ButtonSetting()
    public void openRegisterPage(Setting setting) {
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    Laby.labyAPI().minecraft().chatExecutor()
                        .openUrl("https://ebio.gg/register?redirect=/dashboard/share", false);
                }
            }, 650);
    }

    @MethodOrder(after = "openRegisterPage")
    @SpriteSlot(size = 32, x = 1)
    @TextFieldSetting
    private final ConfigProperty<String> token = new ConfigProperty<>("");

    @MethodOrder(after = "token")
    @SettingSection("check")
    @ButtonSetting()
    public void checkAccount(Setting setting) throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://api.ebio.gg/api/share/status"))
            .header("api-key", token.get())
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();

        HttpClient client = HttpClient.newHttpClient();
        client
            .sendAsync(request, BodyHandlers.ofString())
            .thenAccept((response) -> {
                boolean successful = response.statusCode() >= 200 && response.statusCode() <= 299;
                if (successful) {
                    String used = "NaN", max = "NaN", finalString;
                    try {
                        JsonObject object = JsonParser.parseString(response.body()).getAsJsonObject();

                        used = object.has("used") ? object.get("used").getAsString() : "";
                        max = object.has("max") ? object.get("max").getAsString() : "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    finalString = "You have used " + used + " of " + max + " uploads.";

                    Laby.references().notificationController().push(
                        Notification.builder()
                            .title(Component.text("Success!"))
                            .text(Component.text(finalString))
                            .build());
                } else {
                    Laby.references().notificationController().push(
                        Notification.builder()
                            .title(Component.text("Error!"))
                            .text(Component.text("Something went wrong. Contact help@ebio.gg"))
                            .build());
                }
            })
            .exceptionally((e) -> {
                Laby.references().notificationController().push(
                    Notification.builder()
                        .title(Component.text("Error!"))
                        .text(Component.text(e.getMessage()))
                        .build());
                return null;
            });
    }

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }
    public boolean doubleUploads() {
        return doubleUploads.get();
    }

    public String token() {
        return token.get();
    }
}
