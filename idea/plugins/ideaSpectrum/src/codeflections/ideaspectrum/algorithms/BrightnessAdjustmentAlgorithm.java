package codeflections.ideaspectrum.algorithms;

import codeflections.ideaspectrum.ColorAdjustmentAlgorithm;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author dyadix
 */
@SuppressWarnings("UseJBColor")
public class BrightnessAdjustmentAlgorithm implements ColorAdjustmentAlgorithm {
    private final int change;

    public BrightnessAdjustmentAlgorithm(int change) {
        this.change = change;
    }

    @Override
    public Color adjust(@NotNull Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int newR = adjustComponent(r, change);
        int newG = adjustComponent(g, change);
        int newB = adjustComponent(b, change);
        return new Color(newR , newG, newB);
    }
    
    private int adjustComponent(int component, int change) {
        return Math.max(0, Math.min(255, component + change));
    }
}
