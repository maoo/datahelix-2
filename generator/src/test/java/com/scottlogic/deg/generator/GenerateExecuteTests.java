package com.scottlogic.deg.generator;

import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;
import com.scottlogic.deg.generator.inputs.JsonProfileReader;
import com.scottlogic.deg.generator.outputs.targets.FileOutputTarget;
import com.scottlogic.deg.generator.validators.ErrorReporter;
import com.scottlogic.deg.generator.validators.GenerationConfigValidator;
import com.scottlogic.deg.generator.validators.ValidationResult;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

public class GenerateExecuteTests {

    private GenerationConfig config = mock(GenerationConfig.class);
    private JsonProfileReader profileReader = mock(JsonProfileReader.class);
    private StandardGenerationEngine generationEngine = mock(StandardGenerationEngine.class);
    private GenerationConfigSource configSource = mock(GenerationConfigSource.class);
    private FileOutputTarget outputTarget = mock(FileOutputTarget.class);
    private GenerationConfigValidator validator = mock(GenerationConfigValidator.class);
    private ErrorReporter errorReporter = mock(ErrorReporter.class);
    private ValidationResult mockValidationResult = mock(ValidationResult.class);
    private Profile mockProfile = mock(Profile.class);

    private GenerateExecute excecutor = new GenerateExecute(config, profileReader, generationEngine, configSource,
        outputTarget, validator, errorReporter);

    @Test
    public void invalidConfigCallsCorrectMethods() throws IOException, InvalidProfileException {
        //Arrange
        when(validator.validatePreProfile(config, configSource)).thenReturn(validationResult);
        when(mockValidationResult.isValid()).thenReturn(false);

        //Act
        excecutor.run();

        //Assert
        verify(errorReporter, times(1)).display(mockValidationResult);
        verify(profileReader, never()).read((Path) any());
    }

    @Test
    public void validConfigCallsCorrectMethods() throws IOException, InvalidProfileException {
        //Arrange
        File testFile = new File("TestFile");
        when(validator.validatePreProfile(config, configSource)).thenReturn(validationResult);
        when(configSource.getProfileFile()).thenReturn(testFile);

        when(profileReader.read(eq(testFile.toPath()))).thenReturn(mockProfile);

        when(validator.validateCommandLinePreProfile(eq(config))).thenReturn(mockValidationResult);
        when(validator.validateCommandLinePostProfile(eq(mockProfile))).thenReturn(mockValidationResult);

        when(mockValidationResult.isValid()).thenReturn(true);

        //Act
        excecutor.run();

        //Assert
        verify(profileReader, times(1)).read(testFile.toPath());
        verify(errorReporter, never()).display(any());
    }
}
