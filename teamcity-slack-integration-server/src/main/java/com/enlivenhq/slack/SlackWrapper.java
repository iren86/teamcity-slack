package com.enlivenhq.slack;

import com.enlivenhq.teamcity.SlackNotificator;
import com.enlivenhq.teamcity.SlackPayload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class SlackWrapper {
    public static final GsonBuilder GSON_BUILDER = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
    private static final Logger LOG = Logger.getLogger(SlackNotificator.class);
    protected String slackUrl;

    protected String username;

    protected String channel;

    protected Boolean useAttachment;

    public SlackWrapper() {
        this.useAttachment = TeamCityProperties.getBooleanOrTrue("teamcity.notification.slack.useAttachment");
    }

    public SlackWrapper(Boolean useAttachment) {
        this.useAttachment = useAttachment;
    }

    public String send(BuildInfo info) throws IOException {
        String formattedPayload = getFormattedPayload(info);
        LOG.debug(formattedPayload);

        URL url = new URL(this.getSlackUrl());
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setRequestProperty("User-Agent", "Enliven");
        httpsURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        httpsURLConnection.setDoOutput(true);

        DataOutputStream dataOutputStream = new DataOutputStream(
                httpsURLConnection.getOutputStream()
        );

        dataOutputStream.writeBytes(formattedPayload);
        dataOutputStream.flush();
        dataOutputStream.close();

        InputStream inputStream;
        String responseBody = "";

        try {
            inputStream = httpsURLConnection.getInputStream();
        } catch (IOException e) {
            responseBody = e.getMessage();
            inputStream = httpsURLConnection.getErrorStream();
            if (inputStream != null) {
                responseBody += ": ";
                responseBody = getResponseBody(inputStream, responseBody);
            }
            throw new IOException(responseBody);
        }

        return getResponseBody(inputStream, responseBody);
    }

    @NotNull
    public String getFormattedPayload(BuildInfo info) {
        Gson gson = GSON_BUILDER.create();

        SlackPayload slackPayload = new SlackPayload(info);
        slackPayload.setChannel(getChannel());
        slackPayload.setUsername(getUsername());
        slackPayload.setUseAttachments(this.useAttachment);

        return gson.toJson(slackPayload);
    }

    private String getResponseBody(InputStream inputStream, String responseBody) throws IOException {
        String line;

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );

        while ((line = bufferedReader.readLine()) != null) {
            responseBody += line + "\n";
        }

        bufferedReader.close();
        return responseBody;
    }

    public void setSlackUrl(String slackUrl) {
        this.slackUrl = slackUrl;
    }

    public String getSlackUrl() {
        return this.slackUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }
}
