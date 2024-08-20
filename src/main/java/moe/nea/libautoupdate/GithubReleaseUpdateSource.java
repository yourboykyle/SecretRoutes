package moe.nea.libautoupdate;

import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Update source pulling from a GitHub repositories releases.
 * <p>
 * The stream {@code pre} is dedicated to pre-releases (or full releases, if the newest full release is newer than
 * the newest pre releases).
 * The stream {@code full} is dedicated to only full releases.
 * Override {@link #selectUpdate(String, List)} to change this behaviour.
 * </p>
 * <p>
 * By default the first JAR that is in that release will be selected.
 * Override {@link #findAsset(GithubRelease)} to change this behaviour
 * </p>
 * <p>This {@link UpdateSource} does not support newer than latest releases, since it uses the git tag as update version, and does not support hash checking.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GithubReleaseUpdateSource extends JsonUpdateSource {
    @NonNull
    final String owner;
    @NonNull
    final String repository;

    /**
     * Find an update out of the list of releases from a GitHub repository.
     *
     * @param updateStream the update stream to find the update for
     * @param releases     the list of releases for this GitHub repository
     * @return the latest update that matches this update stream
     */
    protected UpdateData selectUpdate(String updateStream, List<GithubRelease> releases) {
        if (Objects.equals("pre", updateStream)) {
            return findLatestRelease(releases.stream().filter(it -> !it.isDraft()).collect(Collectors.toList()));
        }
        if (Objects.equals("full", updateStream)) {
            return findLatestRelease(releases.stream().filter(it -> !it.isDraft() && !it.isPrerelease()).collect(Collectors.toList()));
        }
        return null;
    }

    /**
     * Find the matching Jar from a GitHub release.
     *
     * @param release a release containing assets
     * @return an update data referencing one of the assets of that release
     */
    protected UpdateData findAsset(GithubRelease release) {
        if (release.getAssets() == null) return null;
        return release.getAssets().stream()
                .filter(it -> (Objects.equals(it.getContentType(), "application/x-java-archive") || (it.getName() != null && it.getName().endsWith(".jar"))) && it.getBrowserDownloadUrl() != null)
                .map(it -> new GithubReleaseUpdateData(
                        release.getName() == null ? release.getTagName() : release.getName(),
                        new JsonPrimitive(release.getTagName()),
                        null,
                        it.getBrowserDownloadUrl(),
                        release.getBody(),
                        release.getTargetCommitish(),
                        release.getCreated_at(),
                        release.getPublishedAt(),
                        release.getHtmlUrl()
                ))
                .findFirst().orElse(null);
    }

    /**
     * Find the latest release out of a list of releases that are valid for an updateStream.
     * Uses {@link #findAsset(GithubRelease)} to find which jar file to use.
     *
     * @param validReleases the list of valid releases
     * @return the latest release (or null)
     */
    protected UpdateData findLatestRelease(Iterable<GithubRelease> validReleases) {
        return StreamSupport.stream(validReleases.spliterator(), false)
                .max(Comparator.comparing(GithubRelease::getPublishedAt))
                .map(this::findAsset)
                .orElse(null);
    }


    protected String getReleaseApiUrl() {
        return String.format("https://api.github.com/repos/%s/%s/releases", owner, repository);
    }

    @Override
    public CompletableFuture<UpdateData> checkUpdate(String updateStream) {
        CompletableFuture<List<GithubRelease>> releases = getJsonFromURL(getReleaseApiUrl(), new TypeToken<List<GithubRelease>>() {
        }.getType());
        return releases.thenApply(it -> it == null ? null : selectUpdate(updateStream, it));
    }

    /**
     * A data class representing the GitHub API response to
     * api.github.com/repos/a/b/releases
     */
    @Data
    public static class GithubRelease {
        @SerializedName("tag_name")
        String tagName;
        @SerializedName("target_commitish")
        String targetCommitish;
        String name;
        boolean draft = false;
        boolean prerelease = false;
        @SerializedName("created_at")
        Date created_at;
        @SerializedName("published_at")
        Date publishedAt;
        int id = 0;
        List<Download> assets;
        String body;
        @SerializedName("html_url")
        String htmlUrl;

        @Data
        public static class Download {
            int id = 0;
            String name;
            @SerializedName("content_type")
            String contentType;
            String label;
            @SerializedName("browser_download_url")
            String browserDownloadUrl;
        }
    }
}
