package com.rappytv.eshare.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rappytv.eshare.EShareAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.I18n;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

public class UploadRequest {

    private boolean successful;
    private String uploadLink;
    protected String error;

    private final File file;
    private final String token;

    public UploadRequest(File file, String token) {
        this.file = file;
        this.token = token;
    }

    public CompletableFuture<Void> sendAsyncRequest() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            MultipartData data = MultipartData.newBuilder().addFile("file", file.toPath(), "image/png").build();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.ebio.gg/api/share/upload"))
                .header("Content-Type", data.getContentType())
                .header("api-key", token)
                .method("POST", data.getBodyPublisher())
                .build();

            HttpClient client = HttpClient.newHttpClient();
            client
                .sendAsync(request, BodyHandlers.ofString())
                .thenAccept((response) -> {
                    successful = response.statusCode() >= 200 && response.statusCode() <= 299;
                    if(successful) {
                        String used = "NaN";
                        String max = "NaN";

                        try {
                            JsonObject object = JsonParser.parseString(response.body()).getAsJsonObject();

                            uploadLink = object.has("url") ? object.get("url").getAsString() : "";
                            if(object.has("used")) used = object.get("used").getAsString();
                            if(object.has("max")) max = object.get("max").getAsString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            uploadLink = "";
                        }
                        Component usage = Component.translatable("eshare.messages.status", Component.text(used), Component.text(max));
                        EShareAddon.notification(
                            Component.translatable("eshare.messages.usage"),
                            usage
                        );
                    } else {
                        try {
                            JsonObject object = JsonParser.parseString(response.body()).getAsJsonObject();

                            error = object.has("message")
                                ? object.get("message").getAsString()
                                : I18n.translate("eshare.errors.empty");
                        } catch (Exception e) {
                            e.printStackTrace();
                            error = I18n.translate("eshare.errors.empty");
                        }
                    }
                    future.complete(null);
                })
                .exceptionally((e) -> {
                    future.completeExceptionally(e);
                    error = e.getMessage();
                    return null;
                });
        } catch (Exception e) {
            error = e.getMessage();
            future.completeExceptionally(e);
        }

        return future;
    }

    public boolean isSuccessful() {
        return successful;
    }
    public String getUploadLink() {
        return uploadLink;
    }
    public String getError() {
        return error;
    }
}