// File: service/AbstractSecureJsonCrudService.java
package ServiceLayer.Interfaces_and_Abstracts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Extension of AbstractJsonCrudService that wraps load/save
 * in AES encryption/decryption so JSON on disk is unreadable.
 *
 * @param <T>  Entity type
 * @param <ID> Identifier type
 */
public abstract class AbstractSecureJsonCrudService<T, ID>
        extends AbstractJsonCrudService<T, ID> {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final SecretKey secretKey;
    private final IvParameterSpec iv;
    private final ObjectMapper mapper = new ObjectMapper();

    protected AbstractSecureJsonCrudService(
            String filePath,
            TypeReference<List<T>> typeRef,
            SecretKey secretKey,
            IvParameterSpec iv) {
        super(filePath, typeRef);
        this.secretKey = secretKey;
        this.iv = iv;
    }

    @Override
    protected void load() {
        File file = new File(getFilePath());
        if (!file.exists())
            return;
        try {
            byte[] cipherText = Files.readAllBytes(file.toPath());
            byte[] jsonBytes = decrypt(cipherText);
            List<T> list = mapper.readValue(jsonBytes, new TypeReference<List<T>>() {
            });
            cache = list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load encrypted data", e);
        }
    }

    @Override
    protected void save() {
        try {
            byte[] jsonBytes = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsBytes(cache);
            byte[] cipherBytes = encrypt(jsonBytes);
            Files.write(new File(getFilePath()).toPath(), cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save encrypted data", e);
        }
    }

    private byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher c = Cipher.getInstance(TRANSFORMATION);
        c.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return c.doFinal(plaintext);
    }

    private byte[] decrypt(byte[] cipherText) throws Exception {
        Cipher c = Cipher.getInstance(TRANSFORMATION);
        c.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return c.doFinal(cipherText);
    }

    /** Subclasses must return the exact file path string passed into super(). */
    protected abstract String getFilePath();
}