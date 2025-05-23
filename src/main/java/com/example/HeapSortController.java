package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.geometry.Bounds;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.animation.RotateTransition;

public class HeapSortController {
    @FXML private TextField inputField;
    @FXML private TextField countField;
    @FXML private Button randomButton;
    @FXML private Button sortButton;
    @FXML private Slider speedSlider;
    @FXML private Label speedValueLabel;
    @FXML private Pane treePane;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button autoButton;
    @FXML private Label statusLabel;
    @FXML private HBox dataArrayContainer;

    private List<int[]> steps;
    private int currentStep = 0;
    private Map<Integer, Circle> nodeCircles;
    private Map<Integer, Label> nodeLabels;
    private Timeline animationTimeline;
    private boolean isAutoPlaying = false;
    private double animationDuration = 0.5;
    private Map<Integer, Circle> dataArrayCircles;
    private Map<Integer, Label> dataArrayLabels;

    @FXML
    public void initialize() {
        // Initialize speed slider
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationDuration = newVal.doubleValue();
            speedValueLabel.setText(String.format("%.1fs", animationDuration));
        });

        // Initialize button actions
        sortButton.setOnAction(e -> handleSort());
        randomButton.setOnAction(e -> handleRandom());
        prevButton.setOnAction(e -> showPreviousStep());
        nextButton.setOnAction(e -> showNextStep());
        autoButton.setOnAction(e -> toggleAutoPlay());
    }

    private void handleSort() {
        try {
            String[] numbers = inputField.getText().split(" ");
            int[] array = new int[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                array[i] = Integer.parseInt(numbers[i].trim());
            }
            startSorting(array);
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers.");
        }
    }

    private void handleRandom() {
        try {
            int count = Integer.parseInt(countField.getText().trim());
            if (count <= 0 || count > 31) {
                showError("Please enter a number between 1 and 31.");
                return;
            }
            int[] randomArray = generateRandomArray(count);
            inputField.setText(arrayToString(randomArray));
            startSorting(randomArray);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        }
    }

    private void toggleAutoPlay() {
        if (isAutoPlaying) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        isAutoPlaying = true;
        autoButton.setText("Stop");
        nextButton.setDisable(true);
        prevButton.setDisable(true);

        animationTimeline = new Timeline(new KeyFrame(Duration.seconds(animationDuration), e -> {
            if (currentStep < steps.size() - 1) {
                showNextStep();
            } else {
                stopAutoPlay();
            }
        }));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }

    private void stopAutoPlay() {
        isAutoPlaying = false;
        autoButton.setText("Auto Play");
        nextButton.setDisable(false);
        prevButton.setDisable(false);
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
    }

    private void startSorting(int[] array) {
        stopAutoPlay();
        steps = new ArrayList<>();
        
        // Add initial empty array
        steps.add(new int[0]);
        
        // Initialize data array display
        initializeDataArray(array);
        
        // Add numbers one by one
        int[] currentArray = new int[0];
        for (int i = 0; i < array.length; i++) {
            currentArray = new int[i + 1];
            System.arraycopy(array, 0, currentArray, 0, i + 1);
            steps.add(currentArray.clone());
            
            // Sort newly added number according to heap property
            int currentIndex = i;
            while (currentIndex > 0) {
                int parentIndex = (currentIndex - 1) / 2;
                // Add comparison step
                steps.add(currentArray.clone());
                
                if (currentArray[currentIndex] > currentArray[parentIndex]) {
                    // Swap
                    int temp = currentArray[currentIndex];
                    currentArray[currentIndex] = currentArray[parentIndex];
                    currentArray[parentIndex] = temp;
                    steps.add(currentArray.clone());
                    currentIndex = parentIndex;
                } else {
                    break;
                }
            }
        }

        // Perform heap sort
        int[] heapArray = currentArray.clone();
        for (int i = heapArray.length - 1; i > 0; i--) {
            // Swap root with last node
            int temp = heapArray[0];
            heapArray[0] = heapArray[i];
            heapArray[i] = temp;
            steps.add(heapArray.clone());

            // Restore heap property
            int currentIndex = 0;
            while (true) {
                int largest = currentIndex;
                int left = 2 * currentIndex + 1;
                int right = 2 * currentIndex + 2;

                // Add comparison step
                steps.add(heapArray.clone());

                if (left < i && heapArray[left] > heapArray[largest]) {
                    largest = left;
                }
                if (right < i && heapArray[right] > heapArray[largest]) {
                    largest = right;
                }

                if (largest != currentIndex) {
                    // Swap
                    temp = heapArray[currentIndex];
                    heapArray[currentIndex] = heapArray[largest];
                    heapArray[largest] = temp;
                    steps.add(heapArray.clone());
                    currentIndex = largest;
                } else {
                    break;
                }
            }
        }
        
        currentStep = 0;
        updateNavigationButtons();
        drawTree();
    }

    private void initializeDataArray(int[] array) {
        dataArrayContainer.getChildren().clear();
        dataArrayCircles = new HashMap<>();
        dataArrayLabels = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            Circle circle = new Circle(20);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);
            
            Label label = new Label(String.valueOf(array[i]));
            label.setStyle("-fx-font-weight: bold;");
            
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(circle, label);
            stackPane.setStyle("-fx-padding: 5;");
            
            dataArrayCircles.put(i, circle);
            dataArrayLabels.put(i, label);
            dataArrayContainer.getChildren().add(stackPane);
        }
    }

    private void animateDataToHeap(int dataIndex, int heapIndex, Runnable onComplete) {
        if (!dataArrayCircles.containsKey(dataIndex) || !nodeCircles.containsKey(heapIndex)) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        Circle dataCircle = dataArrayCircles.get(dataIndex);
        Circle heapCircle = nodeCircles.get(heapIndex);
        Label dataLabel = dataArrayLabels.get(dataIndex);
        Label heapLabel = nodeLabels.get(heapIndex);

        // 애니메이션용 원 생성
        Circle animCircle = new Circle(dataCircle.getRadius());
        animCircle.setFill(Color.HOTPINK);
        animCircle.setStroke(Color.DEEPPINK);
        animCircle.setStrokeWidth(4);

        // 애니메이션용 라벨 생성
        Label animLabel = new Label(dataLabel.getText());
        animLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        StackPane animPane = new StackPane();
        animPane.getChildren().addAll(animCircle, animLabel);

        // 위치 계산
        Bounds dataBounds = dataCircle.localToScene(dataCircle.getBoundsInLocal());
        Bounds heapBounds = heapCircle.localToScene(heapCircle.getBoundsInLocal());
        Bounds containerBounds = treePane.localToScene(treePane.getBoundsInLocal());

        // 애니메이션 패널 위치 설정
        animPane.setLayoutX(dataBounds.getMinX() - containerBounds.getMinX());
        animPane.setLayoutY(dataBounds.getMinY() - containerBounds.getMinY());
        treePane.getChildren().add(animPane);

        // 경로 애니메이션 생성
        Path path = new Path();
        path.getElements().add(new MoveTo(0, 0));
        path.getElements().add(new LineTo(
            heapBounds.getMinX() - dataBounds.getMinX(),
            heapBounds.getMinY() - dataBounds.getMinY()
        ));

        // 경로 애니메이션
        PathTransition pathTransition = new PathTransition(Duration.seconds(animationDuration), path, animPane);
        pathTransition.setOrientation(PathTransition.OrientationType.NONE);

        // 크기 애니메이션
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(animationDuration), animPane);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // 회전 애니메이션
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(animationDuration), animPane);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(360);

        // 투명도 애니메이션
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(animationDuration), animPane);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        // 모든 애니메이션을 함께 실행
        ParallelTransition parallelTransition = new ParallelTransition(
            pathTransition, scaleTransition, rotateTransition, fadeTransition
        );

        // 애니메이션 시작 전에 원본 노드 숨기기
        dataCircle.setVisible(false);
        dataLabel.setVisible(false);

        parallelTransition.setOnFinished(e -> {
            treePane.getChildren().remove(animPane);
            
            // 데이터 배열에서 노드 제거 (페이드 아웃 효과)
            StackPane dataNode = (StackPane) dataArrayContainer.getChildren().get(dataIndex);
            FadeTransition removeTransition = new FadeTransition(Duration.seconds(animationDuration * 0.5), dataNode);
            removeTransition.setFromValue(1.0);
            removeTransition.setToValue(0.0);
            
            removeTransition.setOnFinished(event -> {
                dataArrayContainer.getChildren().remove(dataNode);
                dataArrayCircles.remove(dataIndex);
                dataArrayLabels.remove(dataIndex);
                rebuildDataArray();
                
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            
            removeTransition.play();
        });

        // 애니메이션 시작
        parallelTransition.play();
    }

    private void rebuildDataArray() {
        // Clear the container
        dataArrayContainer.getChildren().clear();
        
        // Get all remaining values
        List<Integer> remainingValues = new ArrayList<>();
        for (int i = 0; i < dataArrayLabels.size(); i++) {
            if (dataArrayLabels.containsKey(i)) {
                remainingValues.add(Integer.parseInt(dataArrayLabels.get(i).getText()));
            }
        }
        
        // Rebuild the display
        for (int i = 0; i < remainingValues.size(); i++) {
            Circle circle = new Circle(20);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);
            
            Label label = new Label(String.valueOf(remainingValues.get(i)));
            label.setStyle("-fx-font-weight: bold;");
            
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(circle, label);
            stackPane.setStyle("-fx-padding: 5;");
            
            dataArrayCircles.put(i, circle);
            dataArrayLabels.put(i, label);
            dataArrayContainer.getChildren().add(stackPane);
        }
    }

    private void drawTree() {
        treePane.getChildren().clear();
        if (steps == null || steps.isEmpty()) return;

        nodeCircles = new HashMap<>();
        nodeLabels = new HashMap<>();
        int[] currentArray = steps.get(currentStep);
        
        // Tree size calculation
        int nodeRadius = 25;
        int levelHeight = 80;
        int treeHeight = (int) (Math.log(currentArray.length) / Math.log(2)) + 1;
        int maxNodesInLevel = (int) Math.pow(2, treeHeight - 1);
        double paneWidth = treePane.getWidth();
        double paneHeight = treePane.getHeight();
        
        // Node spacing calculation
        double horizontalSpacing = paneWidth / (maxNodesInLevel + 1);
        double startX = horizontalSpacing;
        double startY = 50;

        // Draw nodes at each level
        for (int level = 0; level < treeHeight; level++) {
            int nodesInLevel = (int) Math.pow(2, level);
            double levelWidth = nodesInLevel * horizontalSpacing;
            double levelStartX = (paneWidth - levelWidth) / 2 + horizontalSpacing / 2;

            for (int node = 0; node < nodesInLevel; node++) {
                int index = (int) Math.pow(2, level) - 1 + node;
                if (index >= currentArray.length) break;

                double x = levelStartX + node * horizontalSpacing;
                double y = startY + level * levelHeight;

                // Draw circle node
                Circle circle = new Circle(x, y, nodeRadius);
                circle.setFill(Color.LIGHTBLUE);
                circle.setStroke(Color.BLACK);
                nodeCircles.put(index, circle);

                // Draw value label
                Label valueLabel = new Label(String.valueOf(currentArray[index]));
                valueLabel.setLayoutX(x - 10);
                valueLabel.setLayoutY(y - 10);
                nodeLabels.put(index, valueLabel);

                treePane.getChildren().addAll(circle, valueLabel);

                // Draw parent-child connection line
                if (level > 0) {
                    int parentIndex = (index - 1) / 2;
                    Circle parentCircle = nodeCircles.get(parentIndex);
                    
                    if (parentCircle != null) {
                        Line line = new Line();
                        line.setStartX(parentCircle.getCenterX());
                        line.setStartY(parentCircle.getCenterY() + nodeRadius);
                        line.setEndX(x);
                        line.setEndY(y - nodeRadius);
                        line.setStroke(Color.GRAY);
                        treePane.getChildren().add(line);
                    }
                }
            }
        }
        statusLabel.setText(String.format("Step %d / %d", currentStep + 1, steps.size()));
    }

    private void showPreviousStep() {
        if (currentStep > 0) {
            currentStep--;
            updateNavigationButtons();
            drawTree();
        }
    }

    private void showNextStep() {
        if (currentStep < steps.size() - 1) {
            int[] prevArray = steps.get(currentStep);
            int[] nextArray = steps.get(currentStep + 1);
            
            // When new node is added
            if (prevArray.length < nextArray.length) {
                int newIndex = prevArray.length;
                // Draw current state first
                drawTree();
                
                // Animate data from array to heap
                animateDataToHeap(newIndex, newIndex, () -> {
                    // Highlight new node
                    highlightNewNode(newIndex, () -> {
                        // Wait for a while after highlighting new node
                        Timeline waitTimeline = new Timeline(new KeyFrame(Duration.seconds(animationDuration * 2), e -> {
                            currentStep++;
                            updateNavigationButtons();
                            drawTree();
                        }));
                        waitTimeline.play();
                    });
                });
                return;
            }
            
            // Find changed nodes
            boolean foundSwap = false;
            for (int i = 0; i < prevArray.length; i++) {
                if (prevArray[i] != nextArray[i]) {
                    for (int j = 0; j < prevArray.length; j++) {
                        if (j != i && prevArray[j] != nextArray[j]) {
                            final int finalI = i;
                            final int finalJ = j;
                            // Highlight comparing nodes first
                            highlightComparingNodesForSwap(finalI, finalJ, () -> {
                                // Execute swap after comparison
                                animateSwap(finalI, finalJ, () -> {
                                    currentStep++;
                                    updateNavigationButtons();
                                    drawTree();
                                });
                            });
                            foundSwap = true;
                            return;
                        }
                    }
                }
            }
            
            // If no swap, show comparing nodes
            if (!foundSwap) {
                highlightComparingNodes(prevArray, nextArray, () -> {
                    currentStep++;
                    updateNavigationButtons();
                    drawTree();
                });
            }
        }
    }

    private void animateSwap(int index1, int index2, Runnable onComplete) {
        if (nodeCircles == null || !nodeCircles.containsKey(index1) || !nodeCircles.containsKey(index2)) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        Circle circle1 = nodeCircles.get(index1);
        Circle circle2 = nodeCircles.get(index2);
        Label label1 = nodeLabels.get(index1);
        Label label2 = nodeLabels.get(index2);

        // Highlight comparing nodes with yellow color
        circle1.setFill(Color.YELLOW);
        circle2.setFill(Color.YELLOW);

        double x1 = circle1.getCenterX();
        double y1 = circle1.getCenterY();
        double x2 = circle2.getCenterX();
        double y2 = circle2.getCenterY();

        TranslateTransition tt1 = new TranslateTransition(Duration.seconds(animationDuration), circle1);
        TranslateTransition tt2 = new TranslateTransition(Duration.seconds(animationDuration), circle2);
        TranslateTransition tt3 = new TranslateTransition(Duration.seconds(animationDuration), label1);
        TranslateTransition tt4 = new TranslateTransition(Duration.seconds(animationDuration), label2);

        tt1.setToX(x2 - x1);
        tt1.setToY(y2 - y1);
        tt2.setToX(x1 - x2);
        tt2.setToY(y1 - y2);
        tt3.setToX(x2 - x1);
        tt3.setToY(y2 - y1);
        tt4.setToX(x1 - x2);
        tt4.setToY(y1 - y2);

        // Restore colors and execute callback after animation completes
        tt1.setOnFinished(e -> {
            circle1.setFill(Color.LIGHTBLUE);
            circle2.setFill(Color.LIGHTBLUE);
            if (onComplete != null) {
                onComplete.run();
            }
        });

        tt1.play();
        tt2.play();
        tt3.play();
        tt4.play();
    }

    private void highlightComparingNodesForSwap(int index1, int index2, Runnable onComplete) {
        if (nodeCircles == null || !nodeCircles.containsKey(index1) || !nodeCircles.containsKey(index2)) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        Circle circle1 = nodeCircles.get(index1);
        Circle circle2 = nodeCircles.get(index2);

        // Highlight comparing nodes with red color
        circle1.setFill(Color.RED);
        circle2.setFill(Color.RED);

        // Execute callback after animation completes
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(animationDuration), e -> {
            if (onComplete != null) {
                onComplete.run();
            }
        }));
        timeline.play();
    }

    private void highlightComparingNodes(int[] prevArray, int[] nextArray, Runnable onComplete) {
        // Find comparing nodes in current step
        int currentIndex = -1;
        final int parentIndex;
        
        // Comparing in heap construction step
        if (prevArray.length < nextArray.length) {
            currentIndex = prevArray.length;
            parentIndex = (currentIndex - 1) / 2;
        } 
        // Comparing in heap sort step
        else {
            parentIndex = -1;
            for (int i = 0; i < prevArray.length; i++) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                if (left < prevArray.length && prevArray[left] != nextArray[left]) {
                    currentIndex = i;
                    break;
                }
                if (right < prevArray.length && prevArray[right] != nextArray[right]) {
                    currentIndex = i;
                    break;
                }
            }
        }

        final int finalCurrentIndex = currentIndex;
        if (finalCurrentIndex >= 0 && nodeCircles != null) {
            Circle currentCircle = nodeCircles.get(finalCurrentIndex);
            if (currentCircle != null) {
                currentCircle.setFill(Color.ORANGE);
            }
            
            if (parentIndex >= 0) {
                Circle parentCircle = nodeCircles.get(parentIndex);
                if (parentCircle != null) {
                    parentCircle.setFill(Color.ORANGE);
                }
            }

            // Restore colors after animation completes
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(animationDuration), e -> {
                if (currentCircle != null) {
                    currentCircle.setFill(Color.LIGHTBLUE);
                }
                if (parentIndex >= 0 && nodeCircles.get(parentIndex) != null) {
                    nodeCircles.get(parentIndex).setFill(Color.LIGHTBLUE);
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }));
            timeline.play();
        } else {
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    private void highlightNewNode(int index, Runnable onComplete) {
        if (nodeCircles != null && nodeCircles.containsKey(index)) {
            Circle circle = nodeCircles.get(index);
            if (circle != null) {
                // 초기 상태 설정
                circle.setFill(Color.HOTPINK);
                circle.setStroke(Color.DEEPPINK);
                circle.setStrokeWidth(4);
                circle.setOpacity(0.0);
                circle.setScaleX(0.1);
                circle.setScaleY(0.1);
                
                // 애니메이션 효과
                Timeline timeline = new Timeline(
                    // 1단계: 아래에서 위로 올라오면서 나타남
                    new KeyFrame(Duration.seconds(animationDuration * 0.3), e -> {
                        circle.setOpacity(1.0);
                        circle.setScaleX(1.2);
                        circle.setScaleY(1.2);
                    }),
                    // 2단계: 약간 위로 튀어오름
                    new KeyFrame(Duration.seconds(animationDuration * 0.5), e -> {
                        circle.setScaleX(1.4);
                        circle.setScaleY(1.4);
                    }),
                    // 3단계: 원래 크기로 돌아옴
                    new KeyFrame(Duration.seconds(animationDuration * 0.7), e -> {
                        circle.setScaleX(1.0);
                        circle.setScaleY(1.0);
                    }),
                    // 4단계: 최종 상태
                    new KeyFrame(Duration.seconds(animationDuration), e -> {
                        circle.setFill(Color.HOTPINK);
                        circle.setStroke(Color.DEEPPINK);
                        circle.setStrokeWidth(4);
                        circle.setOpacity(1.0);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    })
                );
                timeline.play();
            } else {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        } else {
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    private void updateNavigationButtons() {
        prevButton.setDisable(currentStep <= 0 || isAutoPlaying);
        nextButton.setDisable(currentStep >= steps.size() - 1 || isAutoPlaying);
        statusLabel.setText(String.format("Step %d / %d", currentStep + 1, steps.size()));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int[] generateRandomArray(int count) {
        int[] array = new int[count];
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            array[i] = random.nextInt(100) + 1;  // Random numbers from 1 to 100
        }
        return array;
    }

    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(array[i]);
        }
        return sb.toString();
    }
} 