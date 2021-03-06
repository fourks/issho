package net.perkowitz.issho.hachi.modules.deprecated.minibeat;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.deprecated.minibeat.MinibeatUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class MinibeatDisplay {

    @Setter private GridDisplay display;
    @Getter @Setter private Map<Integer, Color> palette = MinibeatUtil.PALETTE_GREEN;
    @Getter @Setter private int currentFileIndex = 0;
    @Setter private boolean settingsView = false;
    @Setter private boolean isMuted = false;
    @Setter private Integer nextChainStart = null;
    @Setter private Integer nextChainEnd = null;

    public MinibeatDisplay(GridDisplay display) {
        this.display = display;
    }


    /**
     * redraw should know how to draw everything
     *
     * @param memory
     */
    public void redraw(MinibeatMemory memory) {
        if (!settingsView) {
            drawPatterns(memory);
            drawTracks(memory);
            drawSteps(memory);
            drawLeftControls();
            drawValue(0, 7);
        }
    }


    /**
     * initialize should usually not try to initialize the things that Hachi draws
     * (top row of buttons, top button on left side)
     */
    public void initialize() {
        display.initialize(true, Sets.newHashSet(GridButton.Side.Bottom, GridButton.Side.Right));
    }


    /***** draw main view ****************************************/

    public void drawPatterns(MinibeatMemory memory) {

        if (settingsView) return;

        int playingIndex = memory.getPlayingPatternIndex();
        int selectedIndex = memory.getSelectedPatternIndex();

        for (int index = 0; index < MinibeatUtil.PATTERN_COUNT; index++) {

            // draw the playing pattern controls
            GridControl playingControl = MinibeatUtil.patternPlayControls.get(index);
            Color color = palette.get(MinibeatUtil.COLOR_PATTERN);
            if (playingIndex == index) {
                color = palette.get(MinibeatUtil.COLOR_PATTERN_PLAYING);
            } else if (nextChainStart != null && nextChainEnd != null && index >= nextChainStart && index <= nextChainEnd) {
                color = palette.get(MinibeatUtil.COLOR_PATTERN_NEXT);
            } else if (memory.patternIsChained(index)) {
                color = palette.get(MinibeatUtil.COLOR_PATTERN_CHAINED);
            }
            playingControl.draw(display, color);

            // draw the selected pattern controls
            GridControl selectedControl = MinibeatUtil.patternSelectControls.get(index);
            color = palette.get(MinibeatUtil.COLOR_PATTERN_SELECTION);
            if (selectedIndex == index) {
                color = palette.get(MinibeatUtil.COLOR_PATTERN_SELECTED);
            }
            selectedControl.draw(display, color);
        }
    }

    public void drawTracks(MinibeatMemory memory) {
        for (int index = 0; index < MinibeatUtil.TRACK_COUNT; index++) {
              drawTrack(memory, index);
        }
    }

    public void drawTrack(MinibeatMemory memory, int index) {

        if (settingsView) return;

        MinibeatTrack track = memory.getSelectedPattern().getTrack(index);
        boolean enabled = memory.getCurrentSession().trackIsEnabled(index);
        Color color = null;
        if (track.isPlaying() && enabled) {
            color = palette.get(MinibeatUtil.COLOR_TRACK_PLAYING);
        } else if (track.isPlaying() && !enabled) {
            color = palette.get(MinibeatUtil.COLOR_TRACK_PLAYING_MUTED);
        } else if (!track.isPlaying() && enabled) {
            color = palette.get(MinibeatUtil.COLOR_TRACK);
        } else if (!track.isPlaying() && !enabled) {
            color = palette.get(MinibeatUtil.COLOR_TRACK_MUTED);
        }
        GridControl muteControl = MinibeatUtil.trackMuteControls.get(index);
        muteControl.draw(display, color);

        GridControl selectControl = MinibeatUtil.trackSelectControls.get(index);
        color = palette.get(MinibeatUtil.COLOR_TRACK_SELECTION);
        if (track.isPlaying()) {
            color = palette.get(MinibeatUtil.COLOR_TRACK_PLAYING);
        } else if (index == memory.getSelectedTrackIndex()) {
            color = palette.get(MinibeatUtil.COLOR_TRACK_SELECTED);
        }
        selectControl.draw(display, color);
    }

    public void drawSteps(MinibeatMemory memory) {

        if (settingsView) return;

        MinibeatTrack track = memory.getSelectedTrack();

        for (int index = 0; index < MinibeatUtil.STEP_COUNT; index++) {
            GridControl control = MinibeatUtil.stepControls.get(index);
            Color color = palette.get(MinibeatUtil.COLOR_STEP_OFF);
            if (track.getStep(index).isEnabled()) {
                color = palette.get(MinibeatUtil.COLOR_STEP_ON);
            }
            control.draw(display, color);
        }
    }

    public void drawLeftControls() {
        drawControl(settingsControl, settingsView);
        drawControl(muteControl, isMuted);
        drawControl(saveControl, false);
        drawControl(copyControl, false);
    }

    public void drawControl(GridControl control, boolean isOn) {
        if (isOn) {
            control.draw(display, palette.get(COLOR_ON));
        } else {
            control.draw(display, palette.get(COLOR_OFF));
        }
    }

    public void drawValue(int value, int maxValue) {
        int valueAsEight = (value * 8) / maxValue;
        for (int index = 0; index < 8; index++) {
            GridControl control = MinibeatUtil.valueControls.get(index);
            if ((7 - index) <= valueAsEight) {
                control.draw(display, palette.get(MinibeatUtil.COLOR_VALUE_ON));
            } else {
                control.draw(display, palette.get(MinibeatUtil.COLOR_VALUE_OFF));
            }
        }
    }


}
