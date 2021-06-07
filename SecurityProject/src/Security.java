import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class Security {

    //encrypts the password information file
    public static boolean encrypt(BouncyCastleProvider provider, String password, String jsonString) {
        try {
            //generate random iv
            SecureRandom secureRandom = SecureRandom.getInstance("DEFAULT", provider);
            byte[] generatedIV = new byte[16];
            secureRandom.nextBytes(generatedIV);

            //generate key based on the user password entered during the login
            byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 5000, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA256", provider);
            SecretKey key = factory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(generatedIV));

            //in order to encrypt, the data string first needs to be written into pdf
            Files.write(Paths.get("files/passwords.pdf"), jsonString.getBytes());
            byte[] input = Files.readAllBytes(Paths.get("files/passwords.pdf"));
            byte[] output = cipher.doFinal(input);

            //write the encrypted version
            String outFile = "files/information." + Hex.toHexString(generatedIV) + ".aes";
            Files.write(Paths.get(outFile), output);

            //delete the pdf including the passwords
            File pdfFile = new File("files/passwords.pdf");
            pdfFile.delete();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    //decrypts the password information file using the entered password
    //if the file cannot be decrypted, false is returned
    public static boolean decrypt(BouncyCastleProvider provider, String password) {
        File[] files = new File("files").listFiles();
        File encryptedFile = new File("");
        String[] splittedName = new String[5]; //the size is determined as 5 even though the file is expected to contain
        // 3 words when it is split from the "." to give margin to error
        //find the encrypted file
        for (File file : files) {
            if (file.isFile()) {
                String fname = file.getName();
                splittedName = fname.split("\\.");
                if (splittedName[splittedName.length - 1].equals("aes")) {
                    encryptedFile = file;
                    System.out.println(encryptedFile.getName());
                    break;
                }
            }
        }
        if (!encryptedFile.exists()) {
            System.out.println("File not found!");
            return false;
        } else {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);

                //read the file
                byte[] input = Files.readAllBytes(Paths.get("files/" + encryptedFile.getName()));

                //get the iv from the file name
                String ivString = splittedName[1];
                byte[] iv = Hex.decode(ivString);

                //get key from password
                byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
                PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 5000, 128);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA256", provider);
                SecretKey key = factory.generateSecret(keySpec);

                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
                byte[] output = cipher.doFinal(input);

                //write the decrypted file as a json file
                String outFile = "files/" + splittedName[0] + ".json";
                Files.write(Paths.get(outFile), output);

                //delete the encrypted file (because the new information will be encrypted again with a different iv)
                encryptedFile.delete();

            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    //deletes the decrypted password information
    public static void deleteDecryptedFile() {
        File[] files = new File("files").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String[] names = file.getName().split("\\.");
                if (names[names.length - 1].equals("json")) {
                    file.delete();
                }
            }
        }
    }

    //checks if there is an encrypted file
    public static boolean encryptedFileExists() {
        File[] files = new File("files").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                System.out.println(file.getName());
                System.out.println(file.getName().length());
                int charCount = 0;
                for(int i = 0; i < file.getName().length(); i++) {
                    if(file.getName().charAt(i) == '.') {
                        charCount++;
                    }
                }
                System.out.println(charCount);
                String[] names = (file.getName()).split("\\.");
                System.out.println(names.length);
                if(names.length > 0) {
                    if (names[names.length - 1].equals("aes"))
                        return true;
                }
            }
        }
        return false;
    }
}
