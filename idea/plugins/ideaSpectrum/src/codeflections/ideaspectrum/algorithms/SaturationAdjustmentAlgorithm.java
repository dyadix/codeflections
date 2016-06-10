package codeflections.ideaspectrum.algorithms;

import codeflections.ideaspectrum.ColorAdjustmentAlgorithm;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author dyadix
 */
@SuppressWarnings("UseJBColor")
public class SaturationAdjustmentAlgorithm implements ColorAdjustmentAlgorithm {
    private final float factor;

    public SaturationAdjustmentAlgorithm(float factor) {
        this.factor = factor;
    }

    @Override
    public Color adjust(@NotNull Color color) {
        float mean = (color.getRed() + color.getGreen() + color.getBlue()) / 3.f;
        int newR = adjustToMean(color.getRed(), mean);
        int newG = adjustToMean(color.getGreen(), mean);
        int newB = adjustToMean(color.getBlue(), mean);
        return new Color(newR, newG, newB);
    }

    private int adjustToMean(int component, float mean) {
        return Math.max(0, Math.min(255, (int)((component - mean) * factor + mean)));
    }
}
