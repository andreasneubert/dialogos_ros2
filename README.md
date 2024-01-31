# Project to control a Robot via DialogOS

This is a Project to control a Robot via DialogOS. The communication to the robot is based on ROS2. The vizualization of the robot is made in gazebo simulator. 
The communicaton between DialogOS and ROS is based on this plugin: https://github.com/dialogos-project/dialogos-plugin-ros

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

