package moe.nea.libautoupdate;

import lombok.SneakyThrows;
import lombok.Value;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Create {@link UpdateTarget} that deletes the specified Jar, and saves the new Jar in the same folder with the name specified by the download URL.
 */
@Value
public class DeleteAndSaveInSameFolderUpdateTarget implements UpdateTarget {
    /**
     * The file that will be replaced.
     */
    File file;

    @SneakyThrows
    @Override
    public List<UpdateAction> generateUpdateActions(PotentialUpdate update) {
        return Arrays.asList(
                new UpdateAction.DeleteFile(file),
                new UpdateAction.MoveDownloadedFile(update.getUpdateJarStorage(), new File(file.getParentFile(), update.getFileName()))
        );
    }
}
