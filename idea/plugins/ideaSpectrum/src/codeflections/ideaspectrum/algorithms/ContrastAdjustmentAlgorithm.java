package codeflections.ideaspectrum.algorithms;

import codeflections.ideaspectrum.ColorAdjustmentAlgorithm;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author dyadix
 */
@SuppressWarnings("UseJBColor")
public class ContrastAdjustmentAlgorithm implements ColorAdjustmentAlgorithm {
    private final float factor;

    public ContrastAdjustmentAlgorithm(float factor) {
        this.factor = factor;
    }

    @Override
    public Color adjust(@NotNull Color color) {
        int newR = adjustComponent(color.getRed(), factor);
        int newG = adjustComponent(color.getGreen(), factor);
        int newB = adjustComponent(color.getBlue(), factor);
        return new Color(newR, newG, newB);
    }
    
    private int adjustComponent(int component, float factor) {
        return Math.max(0, Math.min(255, (int) (factor * (component - 128f) + 128f)));
    }
}
