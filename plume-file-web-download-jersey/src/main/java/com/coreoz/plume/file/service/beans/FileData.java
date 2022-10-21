package com.coreoz.plume.file.service.beans;

import lombok.Value;

@Value
public class FileData {
    byte[] data;
    String checksum;
    String mimeType;
    String fileName;
}
