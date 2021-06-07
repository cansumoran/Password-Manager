import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityTest {

    @BeforeEach
    void encrypt() {
        Security.encrypt(new BouncyCastleProvider(), "password", "{[]}");
    }

    @Test
    void wrongDecrypt() {
        assertEquals(true,Security.decrypt(new BouncyCastleProvider(), "password"));

    }

    @Test
    void rightDecrypt() {
        assertEquals(false,Security.decrypt(new BouncyCastleProvider(), "wrongpassword"));
    }

    @AfterEach
    void deleteFiles() {
        File[] files = new File("files").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}

