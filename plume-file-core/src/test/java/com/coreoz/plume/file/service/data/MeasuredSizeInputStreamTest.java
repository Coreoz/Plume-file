package com.coreoz.plume.file.service.data;

import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MeasuredSizeInputStreamTest {

    @Test
    public void getInputStreamTotalSize__verify_byte_array_returns_correct_size() throws IOException {
        MeasuredSizeInputStream measuredSizeInputStream = new MeasuredSizeInputStream(
            new ByteArrayInputStream(new byte[127])
        );
        measuredSizeInputStream.readAllBytes();
        assertThat(measuredSizeInputStream.getInputStreamTotalSize()).isEqualTo(127);
    }

    @Test
    public void getInputStreamTotalSize__verify_that_empty_byte_array_returns_0() throws IOException {
        MeasuredSizeInputStream measuredSizeInputStream = new MeasuredSizeInputStream(
            new ByteArrayInputStream(new byte[0])
        );
        measuredSizeInputStream.readAllBytes();
        assertThat(measuredSizeInputStream.getInputStreamTotalSize()).isZero();
    }
}
