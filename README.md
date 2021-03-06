# Human-Activity-Recognition (Android Application)
This is a camera app that continuously detects Human Pose using OpenCV using your devices back camera. The 14 key points detected is then used to detect the Human action. The current application is designed to detect one of the following activies.
- Jumping in Place
- Jumping Jacks
- Bending
- Punching (Boxing)
- Waving (Two Hands)
- Waving (Right)
- Clapping Hands
- Throwing a Ball
- Sitting Down
- Stand Down

## Apk file for testing
| File | Path |
| --- | --- |
| Human Activity Recognition | [APK](/apks/ActivityRecognition16.apk) |

## Model
CPM sample model is used for building first draft code structure. [4]
- [Human Pose Estimation](/app/src/main/assets/humanposemodel.tflite) to the tflite model.
- [Human Activity Recognition](/app/src/main/assets/model16.tflite)

## Performance
The following data is based on testing the application on an android device running Qualcomm Snapdragon 821. Later version of processors are found to have better performance. Further testing is required.

| Function | Average Execution Time (ms) |
| --- | --- |
| Pose Estimation Model | 119ms |
| Action Recognition Model| 2ms |
| Timecost to put values into ByteBuffer | 8ms |
| Timecost to apply GaussBlur and Process HeatMap | 4ms |

To-Do - Work on mace/tflite gpu api for acheiving better performance.

## Design.
The application is designed based on TensorFlow Lite Image Classifier. A breif description of each activity is as follows:
1. **MainActivity** This is the main page. User can navigate to PoseEsimation to open a camera view or to a log file to see predicted classes.
2. **ImageClassifier** An abstract class with functions to 
  - read tflite model file, 
  - byte convertion of Image,
  - Run action recognition model
3. **CameraActivity** Activity to open camera view and draw OpenCV Key Points on Surface View. A function to add a button to take picture will be added here.
4. **ImageClassifierFloatInception** Implements ImageClassifier. Heatmap array is created here. The output that needs to be fed to Tensor Flow lite as outpts. Also defines the execution of action recognition model.

## To Do
- [x] Finish skeleton structure to read tflite file.
- [x] Add structure for Classification.
- [x] Add OpenCV Functions.
- [x] Add Classification Labels
- [ ] Add Log functionality
- [ ] Work on TFlite GPU performance enhancement

## Software Requirement
- Android Studio 3.3.1.
- Android SDK and NDK r16 with minimum API 21.
- OpenCV Library 341.
- tflite "tensorflow-lite:+".
- Cmake and Ninja for compilation.

## SnapShots
<p align="center">
  <img width="200" height="300" src="/Snaps/Screenshot_1553694073.png">
  <img width="200" height="300" src="/Snaps/Screenshot_1553694098.png">
  <img width="200" height="300" src="/Snaps/Screenshot_20190327-095221.jpg">
</p>

## Reference
- [Human Pose Estimation Blog](https://medium.com/tensorflow/real-time-human-pose-estimation-in-the-browser-with-tensorflow-js-7dd0bc881cd5)
- [Object Detection App](https://github.com/tensorflow/examples/tree/master/lite/examples/image_classification/android)
- [TFLite Official Website](https://www.tensorflow.org/lite/models/pose_estimation/overview)
- [Pose Estimation Mobile](https://github.com/edvardHua/PoseEstimationForMobile)
