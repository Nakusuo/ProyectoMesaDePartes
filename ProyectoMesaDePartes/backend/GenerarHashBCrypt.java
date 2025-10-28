import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "12345";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        
        // Verificar que funciona
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificación: " + matches);
    }
}
