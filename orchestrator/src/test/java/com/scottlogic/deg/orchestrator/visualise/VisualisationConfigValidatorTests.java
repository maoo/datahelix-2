package com.scottlogic.deg.orchestrator.visualise;

import com.scottlogic.deg.generator.utils.FileUtils;
import com.scottlogic.deg.orchestrator.validator.VisualisationConfigValidator;
import com.scottlogic.deg.profile.serialisation.ValidationResult;
import com.scottlogic.deg.profile.v0_1.ProfileSchemaValidator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VisualisationConfigValidatorTests {

    private FileUtils mockFileUtils = mock(FileUtils.class);
    private Path mockFilePath = mock(Path.class);
    private VisualisationConfigValidator validator;

    @BeforeEach
    void setup() {
        //Arrange
        validator = new VisualisationConfigValidator(mockFileUtils);
        when(mockFileUtils.isDirectory(eq(mockFilePath))).thenReturn(false);
        when(mockFileUtils.exists(eq(mockFilePath))).thenReturn(false);
    }

    @Test
    public void generateOutputFileAlreadyExists() {
        //Arrange
        when(mockFileUtils.exists(eq(mockFilePath))).thenReturn(true);

        //Act
        ValidationResult validationResult = validator.validateCommandLine(false, mockFilePath);

        //Assert
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void generateOutputFileAlreadyExistsCommandLineOverwrite() {
        //Arrange
        when(mockFileUtils.exists(eq(mockFilePath))).thenReturn(true);

        //Act
        ValidationResult validationResult = validator.validateCommandLine(true, mockFilePath);

        //Assert
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void generateOutputFileDoesNotExist() {

        //Act
        ValidationResult validationResult = validator.validateCommandLine(false, mockFilePath);

        //Assert
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void generateOutputDirNotFile() {
        //Arrange
        when(mockFileUtils.isDirectory(eq(mockFilePath))).thenReturn(true);

        //Act
        ValidationResult validationResult = validator.validateCommandLine(false, mockFilePath);

        //Assert
        Assert.assertFalse(validationResult.isValid());
    }

}
