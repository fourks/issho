package net.perkowitz.issho.hachi.modules.para;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;
import java.util.Set;

import static net.perkowitz.issho.hachi.modules.para.ParaUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class ParaDisplay {

    @Setter private GridDisplay display;
    @Getter @Setter private Map<Integer, Color> palette = ParaUtil.PALETTE_YELLOW;
    @Getter @Setter private boolean settingsMode = false;
    @Getter @Setter private int currentFileIndex = 0;
    @Setter private int currentKeyboardOctave = 5;
    @Setter private boolean stepEditing = false;


    public ParaDisplay(GridDisplay display) {
        this.display = display;
    }

    public void redraw(ParaMemory memory) {
        if (settingsMode) return;
        drawPatterns(memory);
        drawPatternEditControls(false, false);
        drawKeyboard();
        drawSteps(memory, memory.currentPattern().getSteps());
        drawStepEditControls(memory.getStepSelectMode());
    }

    public void drawPatterns(ParaMemory memory) {
        if (settingsMode) return;
        ParaPattern[] patterns = memory.currentSession().getPatterns();
        for (int i = 0; i < patterns.length; i++) {
            drawPattern(memory, patterns[i]);
        }


    }

    public void drawPatternEditControls(boolean copyActive, boolean clearActive) {
        if (settingsMode) return;
        if (copyActive) {
            patternCopyControl.draw(display, palette.get(COLOR_PATTERN_EDIT_SELECTED));
        } else {
            patternCopyControl.draw(display, palette.get(COLOR_PATTERN_EDIT));
        }
        if (clearActive) {
            patternClearControl.draw(display, palette.get(COLOR_PATTERN_EDIT_SELECTED));
        } else {
            patternClearControl.draw(display, palette.get(COLOR_PATTERN_EDIT));
        }
    }

    public void drawPattern(ParaMemory memory, ParaPattern pattern) {

        if (settingsMode) return;
        int index = pattern.getIndex();
        GridControl control = patternControls.get(index);
        Color color = palette.get(COLOR_PATTERN);
        if (memory.getCurrentPatternIndex() == index) {
            color = palette.get(COLOR_PATTERN_PLAYING);
        } else if (index == memory.getPatternChainNextIndex()) {
            color = palette.get(COLOR_PATTERN_CHAINED);
        } else if (index >= memory.getPatternChainMin() && index <= memory.getPatternChainMax()) {
            color = palette.get(COLOR_PATTERN_CHAINED);
        }

        control.draw(display, color);
    }

    public void drawKeyboard() {

        if (settingsMode) return;

        for (GridControl control : keyboardControls.getControls()) {
            Color color = palette.get(COLOR_KEYBOARD_WHITE_KEY);
            if (control.getPad().getY() == KEYBOARD_UPPER_BLACK || control.getPad().getY() == KEYBOARD_LOWER_BLACK) {
                color = palette.get(COLOR_KEYBOARD_BLACK_KEY);
            }
            control.draw(display, color);
        }

        octaveDownControl.draw(display, palette.get(COLOR_KEYBOARD_OCTAVE_DOWN));
        octaveUpControl.draw(display, palette.get(COLOR_KEYBOARD_OCTAVE_UP));
        transposeDownControl.draw(display, palette.get(COLOR_KEYBOARD_TRANSPOSE_DOWN));
        transposeUpControl.draw(display, palette.get(COLOR_KEYBOARD_TRANSPOSE_DOWN));
    }

    public void drawKeyboardNotes(Set<Integer> notes, boolean undraw, boolean stepEditing) {

        if (settingsMode) return;

        Color color = palette.get(COLOR_KEYBOARD_HIGHLIGHT);
        Color octaveDownColor = palette.get(COLOR_KEYBOARD_OCTAVE_DOWN);
        Color octaveUpColor = palette.get(COLOR_KEYBOARD_OCTAVE_UP);
        if (stepEditing) {
            color = palette.get(COLOR_KEYBOARD_SELECTED);
        }
        if (undraw) {
            color = palette.get(COLOR_KEYBOARD_WHITE_KEY);
        }

        int noteRangeLower = currentKeyboardOctave * 12;
        int noteRangeUpper = noteRangeLower + 23;
        for (int note : notes) {
            if (note >= noteRangeLower && note <= noteRangeUpper) {
                int octaveNote = note - noteRangeLower;
                GridControl key = keyboardControls.get(octaveNote);
                key.draw(display, color);
            }
            if (note < noteRangeLower) {
                octaveDownColor = color;
            }
            if (note > noteRangeUpper) {
                octaveUpColor = color;
            }
        }

        octaveDownControl.draw(display, octaveDownColor);
        octaveUpControl.draw(display, octaveUpColor);

    }

    public void drawSteps(ParaMemory memory, ParaStep[] steps) {
        if (settingsMode) return;
        for (int index = 0; index < steps.length; index++) {
            drawStep(memory, steps[index], false);
        }
    }

    public void drawStepOff(ParaStep step) {
        // get step location
        int x = step.getIndex() % 8;
        int y = step.getIndex() / 8 + ParaUtil.STEP_MIN_ROW;
        display.setPad(GridPad.at(x, y), Color.OFF);
    }

    public void drawStep(ParaMemory memory, ParaStep step, boolean highlight) {

        if (settingsMode) return;

        // draw step itself
        int x = step.getIndex() % 8;
        int y = step.getIndex() / 8 + ParaUtil.STEP_MIN_ROW;
        Color stepColor = palette.get(ParaUtil.COLOR_STEP_OFF);
        if (highlight) {
            stepColor = palette.get(ParaUtil.COLOR_STEP_HIGHLIGHT);
        } else if (step.isEnabled()) {
            switch (step.getGate()) {
                case PLAY:
                    stepColor = palette.get(ParaUtil.COLOR_STEP_PLAY);
                    break;
                case TIE:
                    stepColor = palette.get(ParaUtil.COLOR_STEP_TIE);
                    break;
            }
        }
        display.setPad(GridPad.at(x, y), stepColor);

    }

    public void drawStepEditControls(StepSelectMode currentState) {

        if (settingsMode) return;

        // some of the step edit controls are step edit mode buttons
        StepSelectMode[] states = StepSelectMode.values();
        for (int i = 0; i < states.length; i++) {
            GridControl control = stepSelectModeControls.get(i);
            StepSelectMode state = states[i];
            Color color = palette.get(COLOR_MODE_INACTIVE);
            if (state == currentState) {
                color = palette.get(ParaUtil.COLOR_MODE_ACTIVE);
            }
            control.draw(display, color);
        }

        // draw the play/tie setting buttons; draw the play button play-color and the tie button tie-color
        stepGateControls.get(0).draw(display, palette.get(COLOR_STEP_PLAY));
        stepGateControls.get(1).draw(display, palette.get(COLOR_STEP_TIE));

        // and there's some other stuff
//        stepSelectModeControls.get(ParaUtil.STEP_CONTROL_SHIFT_LEFT_INDEX).draw(display, palette.get(COLOR_MODE_INACTIVE));
//        stepSelectModeControls.get(ParaUtil.STEP_CONTROL_SHIFT_RIGHT_INDEX).draw(display, palette.get(COLOR_MODE_INACTIVE));

    }

    public void drawValue(int count, Color color) {
        for (int index = 0; index < count; index++) {
            display.setButton(GridButton.at(GridButton.Side.Right, index), color);
        }
    }

    public void drawFunctions(boolean isMuted) {
        for (GridControl control : functionControls.getControls()) {
            Color color = palette.get(COLOR_MODE_INACTIVE);
            if (control.getIndex() == FUNCTION_SETTINGS_INDEX && settingsMode) {
                color = palette.get(COLOR_MODE_ACTIVE);
            } else if (control.getIndex() == FUNCTION_MUTE_INDEX && isMuted) {
                color = palette.get(COLOR_MODE_ACTIVE);
            }
            control.draw(display, color);
        }
    }

    public boolean toggleSettings() {
        settingsMode = !settingsMode;
        return settingsMode;
    }

    public void initialize() {
        display.initialize();
    }

}
