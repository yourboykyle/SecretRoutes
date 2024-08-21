package moe.nea.libautoupdate;

import lombok.Value;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Value
public class ReplaceJarUpdateTarget implements UpdateTarget {
    File file;

    @Override
    public List<UpdateAction> generateUpdateActions(PotentialUpdate update) {
        return Arrays.asList(
                new UpdateAction.DeleteFile(file),
                new UpdateAction.MoveDownloadedFile(update.getUpdateJarStorage(), file)
        );
    }
}
