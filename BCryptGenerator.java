import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String plainPassword = "12345678";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Encoded Password: " + encodedPassword);
    }
}