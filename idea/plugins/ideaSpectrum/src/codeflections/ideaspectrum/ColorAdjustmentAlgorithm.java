package codeflections.ideaspectrum;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author dyadix
 */
public interface ColorAdjustmentAlgorithm {
    Color adjust(@NotNull Color color);
}
