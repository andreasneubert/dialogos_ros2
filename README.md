# Project to control a Robot via DialogOS

This is a Project to control a Robot via DialogOS. The communication to the robot is based on ROS2. The vizualization of the robot is made in gazebo simulator. 
The communicaton between DialogOS and ROS is based on this plugin: https://github.com/dialogos-project/dialogos-plugin-ros

This graphic shows the original concept of the dialogsystem. In the end of the README you can see the actual implementation in DialogOS and the high agreement between initial concept and final implementation.

![concept](/graphics/conecpt_v1_0.png "communication concept")

# Installation instructions

To run this DialogOS ROS2 functionality you need a Ubuntu 22.04 LTS operating system on your Computer. On the Ubuntu 22.04 you have to install three different things.

At first you have to install ROS2 humble for Ubuntu 22.04 LTS. You can find detailed instructions on how to install on this link: https://docs.ros.org/en/humble/Installation/Ubuntu-Install-Debians.html (Tip: Don't build ros2 from source, it takes more time and doesn't have functional impact here!)

The second step is to install Java. For this you can use open-jdk with the version 17.0.9.
Use the following instruction to install open-jdk:

```
sudo apt update
sudo apt install -y openjdk-17-jdk
```

To test, if the Java version was successfully installed, you can check the installed version with the following command:

```
java --version
```

If the shown version mathes "17.0.3", the installation was successful. 
With that finished, the next step is the installation of Gradle. Our project needs Gradle 8.3 to work. To install Gradle 8.3, use the following commands:

If you haven't installed SDKMAN! yet, you can do this with the following command: (You will need this in order to install Gradle via sdk)
```
curl -s "https://get.sdkman.io" | bash
```

After SDKMAN! is successfully installed, you can use the following instruction to install Gradle:

```
sdk install gradle 8.3
```

To let the ROS2 plugin control a robot, you will need some additional steps which are described in the following tutorial: 

```
https://docs.ros.org/en/rolling/Tutorials/Advanced/Simulators/Gazebo/Gazebo.html
```

You will have to install Gazebo Garden for the interfaces to work. 