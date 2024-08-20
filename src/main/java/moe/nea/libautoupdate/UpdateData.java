package moe.nea.libautoupdate;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateData {
    String versionName;
    JsonElement versionNumber;
    String sha256;
    String download;

    public URL getDownloadAsURL() throws MalformedURLException {
        return new URL(download);
    }
}
