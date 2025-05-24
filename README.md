

# Heap Sort Visualizer

## Overview
This program is an interactive visualization tool that demonstrates the Heap Sort algorithm. It provides a step-by-step visual representation of how a Max Heap is constructed and how the sorting process works.

## Key Features

### 1. Data Input
- Manual input: Users can enter numbers separated by spaces
- Random generation: Users can generate random numbers (1-31 elements, values 1-100)

### 2. Visualization Components
- **Data Array**: Shows the initial input data and its changes during sorting
- **Max Heap Tree**: Displays the heap structure with nodes and connections
- **Status Display**: Shows current phase and progress

### 3. Animation Features
- **Speed Control**: Adjustable animation speed using a slider
- **Step Navigation**: 
  - Manual: Previous/Next buttons for step-by-step control
  - Auto Play: Automatic progression through the sorting steps

### 4. Sorting Process Visualization
1. **Max Heap Construction Phase**
   - Visualizes adding elements to the heap
   - Shows node comparisons and swaps
   - Maintains heap property through animations

2. **Max Heap Completion Phase**
   - Highlights the completed Max Heap structure
   - Transitions to sorting phase

3. **Heap Sort Phase**
   - Demonstrates root node extraction
   - Shows heap property restoration
   - Visualizes the sorting process

4. **Data Array Reconstruction**
   - Displays sorted elements in the data array
   - Uses color coding to distinguish sorted and unsorted elements

### 5. Visual Elements
- **Color Coding**:
  - Light Blue: Unsorted/Active nodes
  - Green: Sorted/Completed nodes
  - Yellow/Red: Nodes being compared
  - Hot Pink: New nodes being added
  - Gray: Used data in the array

### 6. User Interface
- Input field for manual number entry
- Random number generation option
- Speed control slider
- Navigation buttons (Previous, Next, Auto Play)
- Status and phase labels
- Visual tree representation
- Data array display

## Technical Implementation
- Built using JavaFX for the graphical interface
- Implements the Heap Sort algorithm with visual feedback
- Uses animation timelines for smooth transitions
- Maintains separate data structures for visualization and algorithm implementation

## Usage
1. Enter numbers manually or generate random numbers
2. Click "Sort" to start the visualization
3. Use navigation controls to step through the process
4. Adjust speed as needed
5. Observe the transformation of data into a Max Heap and its subsequent sorting

This visualization tool helps users understand the Heap Sort algorithm by providing a clear, step-by-step visual representation of the sorting process.
Heap Sorting Visualiser
