package net.perkowitz.issho.hachi;

import lombok.Getter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.Muteable;
import net.perkowitz.issho.hachi.modules.TextDisplay;
import net.perkowitz.issho.hachi.modules.shihai.ShihaiModule;
import net.perkowitz.issho.util.Terminal;

import static net.perkowitz.issho.hachi.HachiUtil.*;

/**
 * Created by optic on 9/12/16.
 */
public class HachiDeviceManager implements GridListener {

    private Module[] modules = null;
    @Getter private Module activeModule;
    private int activeModuleIndex = 0;
    private GridListener[] moduleListeners = null;
    private GridListener activeListener = null;
    private GridDisplay display;
    private GridDevice gridDevice;
    private HachiController hachiController;


    public HachiDeviceManager(GridDevice gridDevice, Module[] modules, HachiController hachiController) {

        this.modules = modules;
        moduleListeners = new GridListener[modules.length];
        this.gridDevice = gridDevice;
        gridDevice.setListener(this);
        this.display = gridDevice;
        this.hachiController = hachiController;
        for (int i = 0; i < modules.length; i++) {
            moduleListeners[i] = modules[i].getGridListener();
            modules[i].setDisplay(hachiController.getDisplay(i));
        }
        if (modules.length > activeModuleIndex) {
            activeModule = modules[activeModuleIndex];
        }

    }


    /***** private implementation ***************/

    public void selectModule(int index) {
        if (index < modules.length && modules[index] != null) {

            // set this device for the chosen module and not for any other modules
            hachiController.getDisplay(index).add(gridDevice);
            for (int i = 0; i < modules.length; i++) {
                if (i != index) {
                    hachiController.getDisplay(i).remove(gridDevice);
                }
            }

            display = hachiController.getDisplay(index);
            activeModuleIndex = index;
            activeModule = modules[activeModuleIndex];
            activeListener = moduleListeners[index];
            activeModule.redraw();
            redraw();
        }
    }

    public void shutdown() {
        display.initialize();
    }

    public void redraw() {

        // modules
        for (int index = 0; index < modules.length; index++) {
            GridButton button = GridButton.at(HachiUtil.MODULE_BUTTON_SIDE, index);
            Module module = modules[index];
            boolean muted = false;
            if (module == activeModule) {
                display.setButton(button, COLOR_SELECTED);
            } else {
                display.setButton(button, COLOR_UNSET);
            }
// for allowing mutes from top buttons
//            if (module instanceof Muteable) {
//                muted = ((Muteable)modules[index]).isMuted();
//            }
//            if (module == activeModule && !muted) {
//                display.setButton(button, COLOR_SELECTED);
//            } else if (module == activeModule) {
//                display.setButton(button, COLOR_SELECTED_UNSET);
//            } else if (!muted) {
//                display.setButton(button, COLOR_SET);
//            } else {
//                display.setButton(button, COLOR_UNSET);
//            }
        }

        if (hachiController.isClockRunning()) {
            display.setButton(PLAY_BUTTON, COLOR_SET);
        } else {
            display.setButton(PLAY_BUTTON, COLOR_UNSET);
        }

        if (HachiController.isDebugMode()) {
            display.setButton(EXIT_BUTTON, COLOR_UNSET);
        }

        textDraw();
    }

    public void textDraw() {
        Terminal.fg(Terminal.Color.WHITE);
        TextDisplay.clearFrame();
        TextDisplay.drawModules(modules, activeModuleIndex);
        TextDisplay.drawButtons(activeModule.buttonLabels());
        TextDisplay.drawRows(activeModule.rowLabels());
        TextDisplay.drawFrame();
        TextDisplay.drawTime("");

        Terminal.fg(Terminal.Color.GREEN);
    }

    public void textClock(String beat) {
        TextDisplay.drawTime(beat);
    }



    /***** GridListener implementation ***************/

    public void onPadPressed(GridPad pad, int velocity) {
//        System.out.printf("Hachi padPressed: %s, %d\n", pad, velocity);
        if (activeListener != null) {
            activeListener.onPadPressed(pad, velocity);
        }
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("Hachi padRelease: %s\n", pad);
        if (activeListener != null) {
            activeListener.onPadReleased(pad);
        }
    }

    public void onButtonPressed(GridButton button, int velocity) {
//        System.out.printf("Hachi buttonPressed: %s, %d\n", button, velocity);
        GridControl control = new GridControl(button, button.getIndex());
        control.press();
        int index = button.getIndex();
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE && button.getIndex() < modules.length) {
            // wait for button release (but don't pass thru to active module)
            selectModule(button.getIndex());

        } else if (button.equals(PLAY_BUTTON)) {
            hachiController.pressPlay();
            redraw();

        } else if (button.equals(EXIT_BUTTON)) {
            // wait for button release (but don't pass thru to active module)

        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonPressed(button, velocity);
            }
        }
    }

    public void onButtonReleased(GridButton button) {
//        System.out.printf("Hachi buttonReleased: %s\n", button);
        int index = button.getIndex();
        GridControl control = new GridControl(button, index);
        Long elapsed = control.release();
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE) {
            // top row used for module switching
// for allowing mutes from top buttons
//            if (index < modules.length && modules[index] != null) {
//                if (elapsed < MODULE_MUTE_PRESS_IN_MILLIS) {
//                    selectModule(button.getIndex());
//                } else if (modules[index] instanceof Muteable) {
//                    boolean muted = ((Muteable)modules[index]).isMuted();
//                    ((Muteable)modules[index]).mute(!muted);
//                }
//                redraw();
//                if (activeModule instanceof ShihaiModule) {
//                    ((ShihaiModule)activeModule).redraw();
//                }
//            }
        } else if (button.equals(PLAY_BUTTON)) {
            // don't pass thru to active module
        } else if (button.equals(EXIT_BUTTON)) {
            if (elapsed > EXIT_PRESS_IN_MILLIS || HachiController.isDebugMode()) {
                hachiController.pressExit();
            }
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonReleased(button);
            }
        }
    }

    public void onKnobChanged(GridKnob knob, int delta) {}
    public void onKnobSet(GridKnob knob, int value) {}


}
