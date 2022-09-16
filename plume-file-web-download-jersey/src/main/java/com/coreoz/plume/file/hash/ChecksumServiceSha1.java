package com.coreoz.plume.file.hash;

import com.google.common.hash.Hashing;

import javax.inject.Singleton;
import java.util.Base64;

@Singleton
public class ChecksumServiceSha1 implements ChecksumService {

    @Override
    public String hash(byte[] data) {
        if (data == null) {
            return null;
        }

        return Base64
            .getEncoder()
            .encodeToString(
                Hashing
                    .sha256()
                    .hashBytes(data)
                    .asBytes()
            );
    }

}
