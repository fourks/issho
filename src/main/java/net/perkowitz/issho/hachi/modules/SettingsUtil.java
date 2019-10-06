package net.perkowitz.issho.hachi.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.Left;

/**
 * Created by optic on 10/24/16.
 */
public class SettingsUtil {

    public enum SettingsChanged {
        NONE, SELECT_SESSION, LOAD_FILE, SAVE_FILE, SET_MIDI_CHANNEL, SET_SWING,
        OPERATION_STARTED, COPY_SESSION, COPY_SESSION_TO_FILE, CLEAR_SESSION
    }

    /***** locations of various controls on the grid ************************/
    public static int SESSION_MIN_ROW = 0;
    public static int SESSION_MAX_ROW = 1;
    public static int FILE_LOAD_ROW = 2;
    public static int FILE_SAVE_ROW = 3;
    public static int SWING_ROW = 5;
    public static int MIDI_CHANNEL_MIN_ROW = 6;
    public static int MIDI_CHANNEL_MAX_ROW = 7;


    /***** control sets for each function group *****************************/

    public static GridControlSet allNonHachiControls = GridControlSet.fromMultiple(
      GridControlSet.padRows(0, 7),
      GridControlSet.buttonSide(GridButton.Side.Right),
      GridControlSet.buttonSide(GridButton.Side.Bottom),
      GridControlSet.buttonSide(GridButton.Side.Left, 2, 7)
    );
    public static GridControlSet sessionControls = GridControlSet.padRows(SettingsUtil.SESSION_MIN_ROW, SettingsUtil.SESSION_MAX_ROW);
    public static GridControlSet loadControls = GridControlSet.padRows(SettingsUtil.FILE_LOAD_ROW, SettingsUtil.FILE_LOAD_ROW);
    public static GridControlSet saveControls = GridControlSet.padRows(SettingsUtil.FILE_SAVE_ROW, SettingsUtil.FILE_SAVE_ROW);
    public static GridControlSet midiChannelControls = GridControlSet.padRows(SettingsUtil.MIDI_CHANNEL_MIN_ROW, SettingsUtil.MIDI_CHANNEL_MAX_ROW);
    public static GridControlSet swingControls = GridControlSet.padRows(SettingsUtil.SWING_ROW, SettingsUtil.SWING_ROW);

    public static GridControl copyControl = new GridControl(GridButton.at(Left, 2), 0);
    public static GridControl clearControl = new GridControl(GridButton.at(Left, 3), 0);
    public static GridControlSet operationControls = new GridControlSet(Lists.<GridControl>newArrayList(copyControl, clearControl));

    /***** color palettes **********************************************************/

    public static Integer COLOR_OFF = 15;
    public static Integer COLOR_ON = 16;
    public static Integer COLOR_SESSION = 17;
    public static Integer COLOR_SESSION_ACTIVE = 18;
    public static Integer COLOR_SESSION_NEXT = 19;
    public static Integer COLOR_FILE_LOAD = 20;
    public static Integer COLOR_FILE_SAVE = 21;
    public static Integer COLOR_FILE_ACTIVE = 22;
    public static Integer COLOR_MIDI_CHANNEL = 23;
    public static Integer COLOR_MIDI_CHANNEL_ACTIVE = 24;
    public static Integer COLOR_SWING = 25;
    public static Integer COLOR_SWING_ACTIVE = 26;

    public static Map<Integer, Color> PALETTE = Maps.newHashMap();
    static {
        PALETTE.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE.put(COLOR_ON, Color.WHITE);
        PALETTE.put(COLOR_SESSION, Color.DIM_BLUE);
        PALETTE.put(COLOR_SESSION_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_SESSION_NEXT, Color.DARK_GRAY);
        PALETTE.put(COLOR_FILE_LOAD, Color.DIM_GREEN);
        PALETTE.put(COLOR_FILE_SAVE, Color.DIM_RED);
        PALETTE.put(COLOR_FILE_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_MIDI_CHANNEL, Color.DIM_BLUE);
        PALETTE.put(COLOR_MIDI_CHANNEL_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_SWING, Color.DIM_PINK);
        PALETTE.put(COLOR_SWING_ACTIVE, Color.WHITE);
    }





}
