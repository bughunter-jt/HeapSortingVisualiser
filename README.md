# Heap Sort Visualizer

A JavaFX application that provides an interactive visualization of the Heap Sort algorithm.

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Installation and Execution

### 1. Clone the Repository
```bash
git clone https://github.com/bughunter-jt/HeapSortingVisualiser.git
cd HeapSortingVisualiser
```

### 2. Run with Maven
```bash
# Build the project
mvn clean package

# Run the application
mvn javafx:run
```

### 3. Run with JAR file
```bash
# Build and run the JAR file
mvn clean package
java -jar target/heap-sort-visualizer-1.0.0.jar
```

## Key Features

- Interactive visualization of the Heap Sort algorithm
- Real-time animation of the sorting process
- Adjustable animation speed
- Random array generation
- Step-by-step visualization
- Color-coded elements for better understanding

## Development Environment Setup

1. Open the project in your IDE
2. Import as a Maven project
3. Configure Java 17 SDK
4. Update Maven dependencies

## Build Options

### Full Build
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Update Dependencies
```bash
mvn versions:display-dependency-updates
```

## Troubleshooting

### JavaFX Module Error
```bash
# Run with JavaFX module path specified
java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -jar target/heap-sort-visualizer-1.0.0.jar
```

### Insufficient Memory Error
```bash
# Set JVM memory
java -Xmx512m -jar target/heap-sort-visualizer-1.0.0.jar
```

## Visualization Features

### 1. Data Input
- Manual input: Enter numbers separated by spaces
- Random generation: Generate random numbers (1-31 elements, values 1-100)

### 2. Visualization Components
- **Data Array**: Shows initial input and changes during sorting
- **Max Heap Tree**: Displays heap structure with nodes and connections
- **Status Display**: Shows current phase and progress

### 3. Animation Features
- **Speed Control**: Adjustable animation speed using a slider
- **Step Navigation**: 
  - Manual: Previous/Next buttons for step-by-step control
  - Auto Play: Automatic progression through sorting steps

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

## Usage Guide

1. **Starting the Application**
   - Clone the repository
   - Build using Maven
   - Run using `mvn javafx:run`

2. **Using the Visualizer**
   - Enter numbers manually or generate random numbers
   - Click "Sort" to start visualization
   - Use navigation controls to step through the process
   - Adjust speed as needed
   - Observe the transformation of data into a Max Heap

3. **Understanding the Visualization**
   - Watch how elements are added to the heap
   - Observe the heap property maintenance
   - See how the sorting process works
   - Follow the color coding to understand the current state

## Technical Details

- Built with JavaFX for the graphical interface
- Implements the Heap Sort algorithm with visual feedback
- Uses animation timelines for smooth transitions
- Maintains separate data structures for visualization and algorithm implementation

## License

This project is licensed under the Apache License 2.0.

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
