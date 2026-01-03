import java.security.SecureRandom;
import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        // Generate 64 bytes (512 bits) random secret key
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretKey = new byte[64];
        secureRandom.nextBytes(secretKey);
        
        // Convert to Base64 encoding for storage
        String base64Secret = Base64.getEncoder().encodeToString(secretKey);
        
        System.out.println("Generated JWT Secret (Base64 encoded):");
        System.out.println(base64Secret);
        System.out.println("\nSecret length: " + secretKey.length + " bytes (" + (secretKey.length * 8) + " bits)");
    }
}