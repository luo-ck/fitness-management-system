import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestCoachLogin {
    public static void main(String[] args) {
        try {
            // Create URL object
            URL url = new URL("http://localhost:8080/api/auth/test-coach-username");
            // Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Set request method
            conn.setRequestMethod("POST");
            // Set request headers
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            // Allow output
            conn.setDoOutput(true);
            
            // Create request body using UTF-8 bytes directly
            // {"username":"老牛"}
            byte[] requestBodyBytes = {123, 34, 117, 115, 101, 114, 110, 97, 109, 101, 34, 58, 34, -23, -111, -78, -27, -53, -70, 34, 125};
            String expected = new String(requestBodyBytes, StandardCharsets.UTF_8);
            System.out.println("Expected request body: " + expected);
            System.out.println("Request body bytes: " + bytesToHex(requestBodyBytes));
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBodyBytes);
                os.flush();
            }
            
            // Get response code
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // Read response content
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Body: " + response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}