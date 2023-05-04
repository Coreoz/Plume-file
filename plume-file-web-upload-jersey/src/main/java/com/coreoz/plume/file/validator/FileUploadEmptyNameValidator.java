package com.coreoz.plume.file.validator;

public interface FileUploadEmptyNameValidator {
    /**
     * Verify that the file name is not empty:<ul>
     *     <li>"test.txt" -> pass</li>
     *     <li>"" -> fails</li>
     * </ul>
     */
    FileUploadNameLengthValidator fileNameNotEmpty();

    /**
     * Allows files names empty:<ul>
     *     <li>"test.txt" -> pass</li>
     *     <li>"" -> pass</li>
     * </ul>
     */
    default FileUploadNameLengthValidator fileNameAllowEmpty() {
        return (FileUploadNameLengthValidator) this;
    }
}
