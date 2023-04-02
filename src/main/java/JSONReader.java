import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JSONReader {
    public static String readJSON(String path) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String ans;
            StringBuilder stringBuilder = new StringBuilder();
            while ((ans = bufferedReader.readLine()) != null)
                stringBuilder.append(ans);

            return stringBuilder.toString();
        }
    }
}
