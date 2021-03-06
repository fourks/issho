# Overview

Minibeat is a drum sequencer. 

minibeat can hold 16 sessions in memory at a time. 
Each session contains 16 patterns.
Each pattern has 8 instrument tracks.
Each track consists of 16 steps. 
Each step can play a note (with settable velocity) or rest.
The entire contents of memory can be saved to a file. 
Within the Minibeat module, 8 different files can be saved and loaded, but
the files can be copied, backed up, and shared. Data is saved in a JSON format.


# Layout and Controls

## Main View

<img width="600px" src="beat.png"/>

### Standard Controls

- Mute: disables the MIDI output of the sequencer.
- Settings: toggles between the main view and the settings view.
- Save: saves the current memory contents to the current file.

### Patterns

Patterns occupy the first four rows of the grid in drum view. The top two rows are for selecting the playing patterns,
including selecting a range of patterns to be chained. The next two rows are for selecting the pattern for editing. 
Whenever a pattern or chain of patterns is selected for play, the first pattern will be selected for editing, but another pattern can be edited by taooi=


### Tracks

Tracks occupy the 5th and 6th rows of the grid. The 5th row is for muting and unmuting the available tracks. The 6th row
selects the track for editing using the step pads.

Each track corresponds to a single note value sent on a certain MIDI channel. At present, the channel for all tracks is hard-coded to 10. The note numbers are arranged as a keyboard, with the white keys in row 6 and the black keys (and a few extras) in row 5. The note numbers for row 6 are: 36, 38, 40, 41, 43, 45, 47, and 48. The note numbers for row 5 are: 49, 37, 39, 51, 42, 44, 46, and 50. Rhythm has a configuration option to offset 
these note numbers by a certain amount; the offset is applied to all note values. For example, including `"midiNoteOffset": 24` in the
config file will move the whole drumkit up two octaves.

### Steps

Steps occupy the 7th and 8th rows of the grid. Step editing has four modes, selected by the step mode pads: mute, velocity, jump, and play. In mute mode, tapping a step button toggles that step in the track and selects it for velocity editing. In velocity mode, tapping a step selects it for velocity editing without toggling the step's mute state. When a step is selected, the value buttons will display the velocity, and pressing a button will set a new value. Eight velocity values are available, ranging from 15 to 127 in increments of 16.

In jump mode, the sequencer will play that step on its next clock tick, advancing normally from there. The sequence will reset to the first step at the next reset. In play mode, the corresponding sound will be played immediately (not quantized). 


### Value buttons

These buttons can be used to select from a range of values; the purpose varies depending on the context. 
The eight buttons represent eight values in the relevant numeric range, with the lowest value at the bottom and highest value at the top. 
When a step is selected (by tapping it in either mute mode or velocity mode), the value buttons set the velocity of that step. After
selecting a fill pattern for editing, the value buttons set the fill probability.


## Settings View

<img width="600px" src="beat-settings.png"/>

### Sessions

Tap a session pad to select that session from memory. The current session is lit.

### Files

Tap a load pad to load the file with the corresponding number. For example,
if Rhythm is configured to use "rhythm" as a file prefix, pressing the first
pad will load "rhythm-0.json" into memory. Similarly, tap a save pad to save 
the current memory to the corresponding file. The previous file will be moved
to a backup (but only the most recent backup will be retained). The most recently
saved or loaded file will be lit; this is the file that will be written when
tapping the save button in the main view.

### MIDI Channel

These 16 pads correspond to the 16 MIDI channels. Tap one to send the sequencer's
notes on that channel.


# Configuration

Minibeat has a configuration option for the file prefix, used to specify filenames for saving data. 
Minibeat can also be set to use a green or blue color palette. Minibeat uses a default set of
midi notes for its various tracks, corresponding to standard MIDI drum mappings. The ```midiNoteOffset```
setting can be used to transpose all of the tracks by a number of semitones. For example, setting this
value to 24 will transpose all note numbers by two octaves. 

```
  "modules": [
    {
      "class": "MinibeatModule",
      "filePrefix": "beat0",
      "palette": "green",
      "midiNoteOffset": 24
    }
  ]
```

# Color Palette

Rhythm has two defined palettes: green and blue. 

