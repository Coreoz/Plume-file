package com.coreoz.plume.file.service.mimetype;

import com.coreoz.plume.file.services.mimetype.MimeTypesDetector;
import com.coreoz.plume.file.services.mimetype.PeekingInputStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MimeTypesDetectorTest {
    private MimeTypesDetector mimeTypesDetector = new MimeTypesDetector();

    @Test
    public void verify_that_mime_type_detection_works_without_file_name() throws IOException {
        assertThat(this.mimeTypesDetector.guessMimeType(
            null,
            new PeekingInputStream(MimeTypesDetectorTest.class.getResourceAsStream("/white_pixel.png"))
        )).isEqualTo("image/png");
    }

    @Test
    public void verify_that_mime_type_detection_does_not_mess_with_input_stream() throws IOException {
        PeekingInputStream peekingInputStream = new PeekingInputStream(MimeTypesDetectorTest.class.getResourceAsStream("/white_pixel.png"));
        this.mimeTypesDetector.guessMimeType(null, peekingInputStream);
        byte[] pngFirstBytes = fromIntArray(new int[]{ 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A });
        assertThat(peekingInputStream.peekedStream().readNBytes(8)).isEqualTo(pngFirstBytes);
    }

    private static byte[] fromIntArray(int[] values) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < values.length; ++i) {
            baos.write(values[i]);
        }

        return baos.toByteArray();
    }
}
