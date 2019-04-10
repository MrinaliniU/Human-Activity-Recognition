# ARPoseEstimation
This is a camera app that continuously detects Human Pose using OpenCV using your devices back camera.

## Design.
The application is designed based on TensorFlow Lite Image Classifier. A breif description of each activity is as follows:
1. **MainActivity** This is the main page. User can navigate to PoseEsimation to open a camera view or to a log file to see predicted classes.
2. **ImageClassifier** An abstract class with functions to 
  - read tflite model file, 
  - byte convertion of Image,
  - print to K labels
3. **CameraActivity** Activity to open camera view and draw OpenCV Key Points on Surface View. A function to add a button to take picture will be added here.
4. **ImageClassifierFloatInception** Implements ImageClassifier. Heatmap array is created here. The output that needs to be fed to Tensor Flow lite as outpts. TODO // Add function to display inferences.

## Model
CPM sample model is used for building first draft code structure.
![Path](/app/src/main/assets/model.tflite) to the tflite model.

## To Do
- [x] Finish skeleton structure to read tflite file.
- [x] Add structure for Classification.
- [ ] Add OpenCV Functions.
- [ ] Add Classification Labels
- [ ] Add Log functionality

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
- ![Human Pose Estimation Blog](https://medium.com/tensorflow/real-time-human-pose-estimation-in-the-browser-with-tensorflow-js-7dd0bc881cd5)
- ![Object Detection App](https://github.com/tensorflow/examples/tree/master/lite/examples/image_classification/android)
- ![TFLite Official Website](https://www.tensorflow.org/lite/models/pose_estimation/overview)
