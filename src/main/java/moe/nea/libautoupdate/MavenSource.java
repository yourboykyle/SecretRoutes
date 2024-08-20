package moe.nea.libautoupdate;

import com.google.gson.JsonPrimitive;
import lombok.*;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Download source for a maven artifact. Pulls from the maven-metadata versioning info and does not support SNAPSHOT
 * versions.
 */
@Value
@EqualsAndHashCode(callSuper = false)
@NonNull
public class MavenSource implements UpdateSource {
    String repoUrl;
    String module;
    String artifact;
    String classifier;
    String extension;

    protected String getMavenBaseUrl() {
        return String.format("%s/%s/%s", repoUrl, module.replace('.', '/'), artifact);
    }

    protected String getMavenMetadataUrl() {
        return getMavenBaseUrl() + "/maven-metadata.xml";
    }

    protected String getMavenArtifactUrl(String version) {
        return String.format("%s/%s/%s-%s%s%s.%s", getMavenBaseUrl(), version, artifact, version, classifier.isEmpty() ? "" : "-", classifier, extension);
    }

    @SneakyThrows
    @Override
    public CompletableFuture<UpdateData> checkUpdate(String updateStream) {
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var db = dbf.newDocumentBuilder();
        return CompletableFuture.supplyAsync(() -> {
            try (val is = UpdateUtils.openUrlConnection(new URL(getMavenMetadataUrl()))) {
                var document = db.parse(new InputSource(is));
                var metadata = (Element) document.getDocumentElement();
                var versioning = (Element) metadata.getElementsByTagName("versioning").item(0);
                var latest = (Element) versioning.getElementsByTagName("latest").item(0);
                var latestVersion = latest.getTextContent();
                return new UpdateData(
                        latestVersion,
                        new JsonPrimitive(latestVersion),
                        null,
                        getMavenArtifactUrl(latestVersion)
                );
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}
