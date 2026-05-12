package com.sismics.docs.core.util;

import com.google.common.io.ByteStreams;
import com.sismics.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Enhanced tests for EncryptionUtil to improve instruction/branch coverage.
 */
public class TestEncryptUtilEnhanced extends BaseTest {

    @Test
    public void generatePrivateKeyShouldReturnDifferentNonEmptyKeys() {
        String key1 = EncryptionUtil.generatePrivateKey();
        String key2 = EncryptionUtil.generatePrivateKey();

        Assert.assertNotNull(key1);
        Assert.assertNotNull(key2);
        Assert.assertFalse(key1.isEmpty());
        Assert.assertFalse(key2.isEmpty());
        Assert.assertNotEquals("Two generated keys should generally differ", key1, key2);
    }

    @Test
    public void getEncryptionCipherShouldRejectNullAndEmptyKey() throws Exception {
        // null branch
        try {
            EncryptionUtil.getEncryptionCipher(null);
            Assert.fail("Expected IllegalArgumentException for null key");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // empty branch
        try {
            EncryptionUtil.getEncryptionCipher("");
            Assert.fail("Expected IllegalArgumentException for empty key");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // valid key branch
        Cipher cipher = EncryptionUtil.getEncryptionCipher("OnceUponATime");
        Assert.assertNotNull(cipher);
    }

    @Test
    public void decryptFileShouldReturnSamePathWhenPrivateKeyIsNull() throws Exception {
        Path temp = Files.createTempFile("teedy-encrypt-enhanced-", ".bin");
        try {
            byte[] original = "hello teedy".getBytes("UTF-8");
            Files.write(temp, original);

            Path result = EncryptionUtil.decryptFile(temp, null);

            Assert.assertEquals("When privateKey is null, decryptFile must return original file path", temp, result);
            byte[] read = Files.readAllBytes(result);
            Assert.assertArrayEquals(original, read);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    @Test
    public void decryptInputStreamShouldRoundTripEncryptedBytes() throws Exception {
        byte[] plain = "EncryptionUtil round trip content".getBytes("UTF-8");

        Cipher enc = EncryptionUtil.getEncryptionCipher("OnceUponATime");
        InputStream encIs = new CipherInputStream(new ByteArrayInputStream(plain), enc);
        byte[] encrypted = ByteStreams.toByteArray(encIs);

        InputStream decIs = EncryptionUtil.decryptInputStream(new ByteArrayInputStream(encrypted), "OnceUponATime");
        byte[] decrypted = ByteStreams.toByteArray(decIs);

        Assert.assertArrayEquals(plain, decrypted);
    }
}