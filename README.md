## Coursework for CS-256: Visual Computing (Swansea University) - 100/100

The aim is to natively implement functions to apply the following transformations and effects to a given [pre-determined image](https://github.com/user-attachments/assets/b74ee730-9669-40c8-a14c-da502001a927):
  - Gamma Correction
    - Implementation uses an LUT *[30]*
  - Image Resizing
    - Nearest neighbour interpolation *[15]*
    - Bilinear interpolation *[15]*
  - Cross Correlation
    - Implementing a filter with a 5x5 Laplacian matrix *[40]*
      - `-4 -1 +0 -1 -4`<br>
        `-1 -2 +3 +2 -1`<br>
        `+0 +3 +4 +3 +0`<br>
        `-1 +2 +3 +2 -1`<br>
        `-4 -1 +0 -1 -4`

<img src="https://github.com/user-attachments/assets/77d1a3ad-9022-4856-b21f-85bbf5fd5d75" width="700" alt="Application in use, showing the functions">

Project was created using:
  - Java 21
  - OpenJFX JavaFX 21-ea+24 
