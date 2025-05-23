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
    @FXML private Label phaseLabel;

    private List<int[]> steps;
    private int currentStep = 0;
    private Map<Integer, Circle> nodeCircles;
    private Map<Integer, Label> nodeLabels;
    private Timeline animationTimeline;
    private boolean isAutoPlaying = false;
    private double animationDuration = 0.5;
    private Map<Integer, Circle> dataArrayCircles;
    private Map<Integer, Label> dataArrayLabels;
    private int inputArrayLength; // 입력 배열의 길이 저장
    private int maxHeapLastStepIndex; // Max Heap 완성 단계 인덱스

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
        
        // 초기 단계 설정
        phaseLabel.setText("대기 중");
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
        inputArrayLength = array.length;
        maxHeapLastStepIndex = -1;
        steps.add(new int[0]);
        initializeDataArray(array);
        phaseLabel.setText("Max Heap 구성 중");
        statusLabel.setText("데이터를 Max Heap에 추가하는 중...");
        
        // 1. Max Heap 구성 단계
        int[] currentArray = new int[0];
        int[] remainingData = array.clone();
        
        // 데이터 배열에서 하나씩 가져와서 Max Heap 구성
        for (int i = 0; i < array.length; i++) {
            // 새 노드 추가
            int[] newArray = new int[i + 1];
            if (i > 0) {
                System.arraycopy(currentArray, 0, newArray, 0, i);
            }
            newArray[i] = remainingData[0];  // 첫 번째 데이터 사용
            currentArray = newArray;
            steps.add(currentArray.clone());
            
            // 새로 추가된 노드를 부모와 비교하며 힙 속성 유지
            int currentIndex = i;
            while (currentIndex > 0) {
                int parentIndex = (currentIndex - 1) / 2;
                steps.add(currentArray.clone());
                
                if (currentArray[currentIndex] > currentArray[parentIndex]) {
                    // 교환
                    int temp = currentArray[currentIndex];
                    currentArray[currentIndex] = currentArray[parentIndex];
                    currentArray[parentIndex] = temp;
                    steps.add(currentArray.clone());
                    currentIndex = parentIndex;
                } else {
                    break;
                }
            }
            
            // 사용한 데이터 제거
            if (remainingData.length > 1) {
                int[] newRemainingData = new int[remainingData.length - 1];
                System.arraycopy(remainingData, 1, newRemainingData, 0, remainingData.length - 1);
                remainingData = newRemainingData;
            } else {
                remainingData = new int[0];  // 마지막 데이터를 사용한 경우 빈 배열로 설정
            }
            
            // 데이터 배열 시각적 업데이트 (남은 데이터만 표시)
            updateDataArray(remainingData);
        }
        
        // 2. Max Heap 완성 단계
        steps.add(currentArray.clone());
        maxHeapLastStepIndex = steps.size() - 1;
        
        // 3. 힙 정렬 단계
        int[] heapArray = currentArray.clone();
        int heapSize = heapArray.length;
        
        for (int i = heapSize - 1; i > 0; i--) {
            // 루트(최대값)와 마지막 노드 교환 전 상태 저장
            steps.add(heapArray.clone());
            
            // 루트(최대값)와 마지막 노드 교환
            int temp = heapArray[0];
            heapArray[0] = heapArray[i];
            heapArray[i] = temp;
            steps.add(heapArray.clone());
            
            // 힙 속성 복원 (heapify)
            int currentIndex = 0;
            while (true) {
                int largest = currentIndex;
                int left = 2 * currentIndex + 1;
                int right = 2 * currentIndex + 2;
                
                // 비교 단계 추가
                steps.add(heapArray.clone());
                
                // 현재 노드와 자식 노드들 중 최대값 찾기
                if (left < i && heapArray[left] > heapArray[largest]) {
                    largest = left;
                }
                if (right < i && heapArray[right] > heapArray[largest]) {
                    largest = right;
                }
                
                // 최대값이 현재 노드가 아니면 교환
                if (largest != currentIndex) {
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
        
        // 4. 트리 순회 및 데이터 배열 재구성 단계
        int[] sortedArray = new int[heapArray.length];
        for (int i = 0; i < heapArray.length; i++) {
            sortedArray[i] = heapArray[i];
            steps.add(heapArray.clone());  // 현재 힙 상태 저장
            updateDataArrayWithSortedData(sortedArray, i + 1);  // 정렬된 데이터로 배열 업데이트
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

    private void updateDataArray(int[] remainingData) {
        dataArrayContainer.getChildren().clear();
        dataArrayCircles.clear();
        dataArrayLabels.clear();
        
        for (int i = 0; i < remainingData.length; i++) {
            Circle circle = new Circle(20);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.BLACK);
            
            Label label = new Label(String.valueOf(remainingData[i]));
            label.setStyle("-fx-font-weight: bold;");
            
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(circle, label);
            stackPane.setStyle("-fx-padding: 5;");
            
            dataArrayCircles.put(i, circle);
            dataArrayLabels.put(i, label);
            dataArrayContainer.getChildren().add(stackPane);
        }
    }

    private void updateDataArrayVisual(int[] originalArray, int usedCount) {
        dataArrayContainer.getChildren().clear();
        dataArrayCircles.clear();
        dataArrayLabels.clear();
        
        for (int i = 0; i < originalArray.length; i++) {
            Circle circle = new Circle(20);
            if (i < usedCount) {
                circle.setFill(Color.GRAY);  // 사용된 데이터는 회색으로 표시
            } else {
                circle.setFill(Color.LIGHTBLUE);  // 사용되지 않은 데이터는 파란색으로 표시
            }
            circle.setStroke(Color.BLACK);
            
            Label label = new Label(String.valueOf(originalArray[i]));
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
        if (dataArrayContainer == null || dataArrayContainer.getChildren().isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        // dataIndex가 유효한 범위 내에 있는지 확인
        if (dataIndex < 0 || dataIndex >= dataArrayContainer.getChildren().size()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        if (!dataArrayCircles.containsKey(dataIndex)) {
            if (onComplete != null) onComplete.run();
            return;
        }

        Circle dataCircle = dataArrayCircles.get(dataIndex);
        Label dataLabel = dataArrayLabels.get(dataIndex);

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
        Bounds containerBounds = treePane.localToScene(treePane.getBoundsInLocal());

        // 힙 트리의 목적지 위치 계산
        double heapX = calculateHeapNodeX(heapIndex);
        double heapY = calculateHeapNodeY(heapIndex);

        // 애니메이션 패널 위치 설정
        animPane.setLayoutX(dataBounds.getMinX() - containerBounds.getMinX());
        animPane.setLayoutY(dataBounds.getMinY() - containerBounds.getMinY());
        treePane.getChildren().add(animPane);

        // 경로 애니메이션 생성
        Path path = new Path();
        path.getElements().add(new MoveTo(0, 0));
        path.getElements().add(new LineTo(
            heapX - (dataBounds.getMinX() - containerBounds.getMinX()),
            heapY - (dataBounds.getMinY() - containerBounds.getMinY())
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
            if (dataIndex < dataArrayContainer.getChildren().size()) {
                StackPane dataNode = (StackPane) dataArrayContainer.getChildren().get(dataIndex);
                FadeTransition removeTransition = new FadeTransition(Duration.seconds(animationDuration * 0.5), dataNode);
                removeTransition.setFromValue(1.0);
                removeTransition.setToValue(0.0);
                
                removeTransition.setOnFinished(event -> {
                    dataArrayContainer.getChildren().remove(dataNode);
                    dataArrayCircles.remove(dataIndex);
                    dataArrayLabels.remove(dataIndex);
                    rebuildDataArray();
                    if (onComplete != null) onComplete.run();
                });
                
                removeTransition.play();
            } else {
                if (onComplete != null) onComplete.run();
            }
        });

        parallelTransition.play();
    }

    private double calculateHeapNodeX(int heapIndex) {
        // 힙 트리의 레벨 계산
        int level = (int) (Math.log(heapIndex + 1) / Math.log(2));
        
        // 현재 레벨에서의 노드 위치 계산
        int positionInLevel = heapIndex - (int) Math.pow(2, level) + 1;
        
        // 현재 레벨의 최대 노드 수
        int maxNodesInLevel = (int) Math.pow(2, level);
        
        // 트리 패널의 너비
        double paneWidth = treePane.getWidth();
        
        // 노드 간의 수평 간격 계산
        double horizontalSpacing = paneWidth / (maxNodesInLevel + 1);
        
        // 시작 X 좌표 계산 (중앙 정렬)
        double startX = (paneWidth - (maxNodesInLevel * horizontalSpacing)) / 2;
        
        // 최종 X 좌표 계산
        return startX + (positionInLevel * horizontalSpacing) + (horizontalSpacing / 2);
    }

    private double calculateHeapNodeY(int heapIndex) {
        // 힙 트리의 레벨 계산
        int level = (int) (Math.log(heapIndex + 1) / Math.log(2));
        
        // 레벨 간의 수직 간격
        int levelHeight = 80;
        
        // 시작 Y 좌표
        double startY = 50;
        
        // 최종 Y 좌표 계산
        return startY + (level * levelHeight);
    }

    private void rebuildDataArray() {
        // Get all remaining values before clearing
        List<Integer> remainingValues = new ArrayList<>();
        for (Map.Entry<Integer, Label> entry : dataArrayLabels.entrySet()) {
            remainingValues.add(Integer.parseInt(entry.getValue().getText()));
        }
        
        // Clear the container and maps
        dataArrayContainer.getChildren().clear();
        dataArrayCircles.clear();
        dataArrayLabels.clear();
        
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
        if (steps == null || steps.isEmpty() || currentStep >= steps.size()) {
            System.out.println("Invalid step state: steps=" + (steps == null ? "null" : steps.size()) + ", currentStep=" + currentStep);
            if (steps != null && !steps.isEmpty()) {
                currentStep = steps.size() - 1;
                updateNavigationButtons();
            }
            return;
        }
        treePane.getChildren().clear();
        nodeCircles = new HashMap<>();
        nodeLabels = new HashMap<>();
        int[] currentArray = steps.get(currentStep);
        if (currentArray == null) {
            System.out.println("Current array is null at step " + currentStep);
            return;
        }
        // 단계별 메시지
        if (currentStep < maxHeapLastStepIndex) {
            phaseLabel.setText("Max Heap 구성 중");
            statusLabel.setText(String.format("데이터를 Max Heap에 추가하는 중... (%d/%d)", currentArray.length, inputArrayLength));
        } else if (currentStep == maxHeapLastStepIndex) {
            phaseLabel.setText("Max Heap 구성 완료");
            statusLabel.setText("Max Heap 구성이 완료되었습니다. 정렬을 시작합니다.");
        } else if (currentStep > maxHeapLastStepIndex && currentStep < steps.size() - 1) {
            phaseLabel.setText("정렬 중");
            statusLabel.setText(String.format("정렬 중... (Step %d / %d)", currentStep - maxHeapLastStepIndex, steps.size() - maxHeapLastStepIndex));
        } else {
            phaseLabel.setText("정렬 완료");
            statusLabel.setText("정렬 완료!");
        }
        int nodeRadius = 25;
        int levelHeight = 80;
        int treeHeight = 0;
        int totalNodes = 0;
        while (totalNodes < currentArray.length) {
            totalNodes += (int) Math.pow(2, treeHeight);
            treeHeight++;
        }
        int maxNodesInLevel = (int) Math.pow(2, treeHeight - 1);
        double paneWidth = treePane.getWidth();
        double horizontalSpacing = paneWidth / (maxNodesInLevel + 1);
        double startY = 50;
        for (int level = 0; level < treeHeight; level++) {
            int nodesInLevel = (int) Math.pow(2, level);
            double levelWidth = nodesInLevel * horizontalSpacing;
            double levelStartX = (paneWidth - levelWidth) / 2 + horizontalSpacing / 2;
            for (int nodeIndex = 0; nodeIndex < nodesInLevel; nodeIndex++) {
                int index = (int) Math.pow(2, level) - 1 + nodeIndex;
                if (index >= currentArray.length) break;
                double x = levelStartX + nodeIndex * horizontalSpacing;
                double y = startY + level * levelHeight;
                Circle circle = new Circle(x, y, nodeRadius);
                if (currentStep < maxHeapLastStepIndex) {
                    circle.setFill(Color.LIGHTBLUE);
                } else if (currentStep == maxHeapLastStepIndex) {
                    circle.setFill(Color.GREEN);
                } else {
                    circle.setFill(Color.LIGHTGREEN);
                }
                circle.setStroke(Color.BLACK);
                nodeCircles.put(index, circle);
                Label valueLabel = new Label(String.valueOf(currentArray[index]));
                valueLabel.setLayoutX(x - 10);
                valueLabel.setLayoutY(y - 10);
                nodeLabels.put(index, valueLabel);
                treePane.getChildren().addAll(circle, valueLabel);
                if (level > 0) {
                    int parentIndex = (index - 1) / 2;
                    if (parentIndex >= 0 && parentIndex < currentArray.length) {
                        Circle parentCircle = nodeCircles.get(parentIndex);
                        if (parentCircle != null) {
                            Line line = new Line();
                            line.setStartX(parentCircle.getCenterX());
                            line.setStartY(parentCircle.getCenterY() + nodeRadius);
                            line.setEndX(x);
                            line.setEndY(y - nodeRadius);
                            if (currentStep < maxHeapLastStepIndex) {
                                line.setStroke(Color.GRAY);
                            } else if (currentStep == maxHeapLastStepIndex) {
                                line.setStroke(Color.GREEN);
                            } else {
                                line.setStroke(Color.DARKGREEN);
                            }
                            treePane.getChildren().add(line);
                        }
                    }
                }
            }
        }
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
            
            // Max Heap 구성 단계
            if (currentStep < maxHeapLastStepIndex) {
                phaseLabel.setText("Max Heap 구성 중");
                statusLabel.setText(String.format("데이터를 Max Heap에 추가하는 중... (%d/%d)", nextArray.length, inputArrayLength));
                
                // 새로운 노드가 추가되는 경우
                if (prevArray.length < nextArray.length) {
                    int newIndex = prevArray.length;
                    
                    // 데이터 배열이 비어있는 경우 처리
                    if (dataArrayContainer.getChildren().isEmpty()) {
                        currentStep++;
                        updateNavigationButtons();
                        drawTree();
                        return;
                    }

                    // 실제로 data array에 남아있는 값 중, nextArray[newIndex]와 같은 값을 찾아 인덱스를 사용
                    int valueToAdd = nextArray[newIndex];
                    int foundIndex = -1;
                    for (int i = 0; i < dataArrayContainer.getChildren().size(); i++) {
                        Label label = (Label) ((StackPane) dataArrayContainer.getChildren().get(i)).getChildren().get(1);
                        if (label.getText().equals(String.valueOf(valueToAdd))) {
                            foundIndex = i;
                            break;
                        }
                    }
                    
                    // foundIndex가 유효한 범위 내에 있는지 확인
                    if (foundIndex >= 0 && foundIndex < dataArrayContainer.getChildren().size()) {
                        int dataArrayIndex = foundIndex;
                        animateDataToHeap(dataArrayIndex, newIndex, () -> {
                            highlightNewNode(newIndex, () -> {
                                int parentIndex = (newIndex - 1) / 2;
                                if (parentIndex >= 0) {
                                    phaseLabel.setText("부모 노드와 비교 중");
                                    statusLabel.setText("새로 추가된 노드를 부모 노드와 비교하는 중...");
                                    highlightComparingNodesForSwap(newIndex, parentIndex, () -> {
                                        currentStep++;
                                        updateNavigationButtons();
                                        drawTree();
                                    });
                                } else {
                                    currentStep++;
                                    updateNavigationButtons();
                                    drawTree();
                                }
                            });
                        });
                    } else {
                        // 유효한 인덱스를 찾지 못한 경우 다음 단계로 진행
                        currentStep++;
                        updateNavigationButtons();
                        drawTree();
                    }
                    return;
                }
                
                // 노드 교환 단계
                for (int i = 0; i < prevArray.length; i++) {
                    if (prevArray[i] != nextArray[i]) {
                        for (int j = 0; j < prevArray.length; j++) {
                            if (j != i && prevArray[j] != nextArray[j]) {
                                final int finalI = i;
                                final int finalJ = j;
                                highlightComparingNodesForSwap(finalI, finalJ, () -> {
                                    animateSwap(finalI, finalJ, () -> {
                                        currentStep++;
                                        updateNavigationButtons();
                                        drawTree();
                                    });
                                });
                                return;
                            }
                        }
                    }
                }
                
                // 비교만 있는 경우
                highlightComparingNodes(prevArray, nextArray, () -> {
                    currentStep++;
                    updateNavigationButtons();
                    drawTree();
                });
                return;
            }
            
            // Max Heap 완성 단계
            if (currentStep == maxHeapLastStepIndex) {
                phaseLabel.setText("Max Heap 구성 완료");
                statusLabel.setText("Max Heap 구성이 완료되었습니다. 정렬을 시작합니다.");
                highlightMaxHeapComplete(() -> {
                    currentStep++;
                    updateNavigationButtons();
                    drawTree();
                });
                return;
            }
            
            // 정렬 단계
            if (currentStep > maxHeapLastStepIndex) {
                phaseLabel.setText("정렬 중");
                statusLabel.setText(String.format("정렬 중... (Step %d / %d)", 
                    currentStep - maxHeapLastStepIndex, 
                    steps.size() - maxHeapLastStepIndex - 1));
                
                // 최초 정렬 단계인 경우
                if (currentStep == maxHeapLastStepIndex + 1) {
                    phaseLabel.setText("최초 정렬 시작");
                    statusLabel.setText("루트 노드(최대값)와 마지막 노드를 교환합니다.");
                    highlightComparingNodesForSwap(0, prevArray.length - 1, () -> {
                        animateSwap(0, prevArray.length - 1, () -> {
                            currentStep++;
                            updateNavigationButtons();
                            drawTree();
                        });
                    });
                    return;
                }
                
                // 노드 교환 확인
                boolean foundSwap = false;
                for (int i = 0; i < prevArray.length; i++) {
                    if (prevArray[i] != nextArray[i]) {
                        for (int j = 0; j < prevArray.length; j++) {
                            if (j != i && prevArray[j] != nextArray[j]) {
                                final int finalI = i;
                                final int finalJ = j;
                                highlightComparingNodesForSwap(finalI, finalJ, () -> {
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
                
                // 비교만 있는 경우
                if (!foundSwap) {
                    highlightComparingNodes(prevArray, nextArray, () -> {
                        currentStep++;
                        updateNavigationButtons();
                        drawTree();
                    });
                }
            }
        } else {
            phaseLabel.setText("정렬 완료");
            statusLabel.setText("정렬 완료!");
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
                        circle.setFill(Color.LIGHTBLUE);
                        circle.setStroke(Color.BLACK);
                        circle.setStrokeWidth(1);
                        circle.setOpacity(1.0);
                        if (onComplete != null) onComplete.run();
                    })
                );
                timeline.play();
            } else {
                if (onComplete != null) onComplete.run();
            }
        } else {
            if (onComplete != null) onComplete.run();
        }
    }

    private void highlightMaxHeapComplete(Runnable onComplete) {
        // 모든 노드를 초록색으로 강조
        for (Circle circle : nodeCircles.values()) {
            circle.setFill(Color.GREEN);
        }
        
        // 1초 후에 원래 색상으로 복원
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            for (Circle circle : nodeCircles.values()) {
                circle.setFill(Color.LIGHTBLUE);
            }
            if (onComplete != null) {
                onComplete.run();
            }
        }));
        timeline.play();
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

    private void updateDataArrayWithSortedData(int[] sortedArray, int currentIndex) {
        dataArrayContainer.getChildren().clear();
        dataArrayCircles.clear();
        dataArrayLabels.clear();
        
        for (int i = 0; i < sortedArray.length; i++) {
            Circle circle = new Circle(20);
            if (i < currentIndex) {
                circle.setFill(Color.GREEN);  // 정렬된 데이터는 초록색으로 표시
            } else {
                circle.setFill(Color.LIGHTBLUE);  // 아직 정렬되지 않은 데이터는 파란색으로 표시
            }
            circle.setStroke(Color.BLACK);
            
            Label label = new Label(String.valueOf(sortedArray[i]));
            label.setStyle("-fx-font-weight: bold;");
            
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(circle, label);
            stackPane.setStyle("-fx-padding: 5;");
            
            dataArrayCircles.put(i, circle);
            dataArrayLabels.put(i, label);
            dataArrayContainer.getChildren().add(stackPane);
        }
    }
} 