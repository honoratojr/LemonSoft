package lemonsoft.senac.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Util {

    public static String md5(String senha) throws NoSuchAlgorithmException {

        try {
            MessageDigest messagedig = MessageDigest.getInstance("MD5");
            byte[] digest = messagedig.digest(senha.getBytes());
            BigInteger hash = new BigInteger(1, digest);
            return String.format("%032x", hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro na criptografia da senha", e);
        }
    }

}
/*A quem possa interessar...
 * Essa classe é utilizada para criar o hash de criptografia das senhas, embora existam métodos mais seguros que o MD5, 
 * como o ByCrypt por exemplo, esse método funcionou para esse projeto.
 * Obs.: Verificar outro método de criptografia de senhas.
 */