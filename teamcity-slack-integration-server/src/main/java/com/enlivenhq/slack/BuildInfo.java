package com.enlivenhq.slack;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BuildInfo {
    private final String username;
    private final String fullname;
    private final String email;
    private final String project;
    private final String build;
    private final String branch;
    private final String statusText;
    private final String statusColor;
    private final String btId;
    private final long buildId;
    private final String serverUrl;
    private final List<Changeset> changesetList;

    private BuildInfo(String username, String fullname, String email, String project, String build, String branch,
                      String statusText, String statusColor, String btId, long buildId, String serverUrl, List<Changeset> changesetList) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.project = project;
        this.build = build;
        this.branch = branch;
        this.statusText = statusText;
        this.statusColor = statusColor;
        this.btId = btId;
        this.buildId = buildId;
        this.serverUrl = serverUrl;
        this.changesetList = Collections.unmodifiableList(changesetList);
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getProject() {
        return project;
    }

    public String getBuild() {
        return build;
    }

    public String getBranch() {
        return branch;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public String getBtId() {
        return btId;
    }

    public long getBuildId() {
        return buildId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public List<Changeset> getChangesetList() {
        return changesetList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BuildInfo{");
        sb.append("username='").append(username).append('\'');
        sb.append(", fullname='").append(fullname).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", project='").append(project).append('\'');
        sb.append(", build='").append(build).append('\'');
        sb.append(", branch='").append(branch).append('\'');
        sb.append(", statusText='").append(statusText).append('\'');
        sb.append(", statusColor='").append(statusColor).append('\'');
        sb.append(", btId='").append(btId).append('\'');
        sb.append(", buildId=").append(buildId);
        sb.append(", serverUrl='").append(serverUrl).append('\'');
        sb.append(", changesetList=").append(changesetList);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private String username;
        private String fullname;
        private String email;
        private String project;
        private String build;
        private String branch;
        private String statusText;
        private String statusColor;
        private String btId;
        private long buildId;
        private String serverUrl;
        private List<Changeset> changesetList;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder project(String project) {
            this.project = project;
            return this;
        }

        public Builder build(String build) {
            this.build = build;
            return this;
        }

        public Builder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public Builder statusColor(String statusColor) {
            this.statusColor = statusColor;
            return this;
        }

        public Builder btId(String btId) {
            this.btId = btId;
            return this;
        }

        public Builder buildId(long buildId) {
            this.buildId = buildId;
            return this;
        }

        public Builder serverUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder changesetList(List<Changeset> changesetList) {
            this.changesetList = changesetList;
            return this;
        }

        public BuildInfo createBuildInfo() {
            Objects.requireNonNull(username);
            Objects.requireNonNull(fullname);
            Objects.requireNonNull(email);
            Objects.requireNonNull(project);
            Objects.requireNonNull(build);
            Objects.requireNonNull(branch);
            Objects.requireNonNull(statusText);
            Objects.requireNonNull(statusColor);
            Objects.requireNonNull(btId);
            Objects.requireNonNull(serverUrl);
            Objects.requireNonNull(changesetList);

            return new BuildInfo(username, fullname, email, project, build, branch, statusText,
                    statusColor, btId, buildId, serverUrl, changesetList);
        }
    }
}
