# Project to control a Robot via DialogOS

This is a Project to control a Robot via DialogOS. The communication to the robot is based on ROS2. The vizualization of the robot is made in gazebo simulator. 
The communicaton between DialogOS and ROS is based on this plugin: https://github.com/dialogos-project/dialogos-plugin-ros

This graphic shows the original concept of the dialogsystem. In the end of the README you can see the actual implementation in DialogOS and the high agreement between initial concept and final implementation.

![concept](/graphics/conecpt_v1_0.png "communication concept")

# Installation instructions

To run this DialogOS ROS2 functionality you need a Ubuntu 22.04 LTS operating system on your Computer. On the Ubuntu 22.04 you have to install three different things.

At first you have to install ROS2 humble for Ubuntu 22.04 LTS. You can find detailed instructions on how to install on this link:

```
https://docs.ros.org/en/humble/Installation/Ubuntu-Install-Debians.html
```
(Tip: Don't build ros2 from source, it takes more time and doesn't have functional impact here!)

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

To let the ROS2 plugin control a robot, you will need Gazebo Garden and a installed ros-gz-bridge in order to control the robot.

The following link describes the installation of Gazebo. You will have to install Gazebo Garden or Fortress for the interfaces to work: 

```
https://gazebosim.org/docs/garden/install_ubuntu
```

This command installs the nescessary ros-gz-bridge:

```
sudo apt-get install ros-rolling-ros-ign-bridge
```

Now place the world "dialogos_robot.sdf" from the repo in the following directory:

```
/usr/share/ignition/ignition-gazebo6/worlds/
```

With these steps finished the setup is completed.

# Running the Project 

**Remeber to allways source ros2 in every terminal you are working on! (Alternatively you can write the command in your .bashrc)**

```
source /opt/ros/humble/setup.bash
```


When everything is successfully installed, the project should build flawless. To build the project, locate a terminal to the cloned repository and change to the "Code"-folder.
Now execute the following command: 

```
./gradlew build
```

Building the project for the first time needs some time (like 1-2 minutes). If the build was successful you can run the plugin with the following command:

```
./gradlew run
```

Now DialogOS should open and you can load our example XML-file from the repo. This file allows you a basic control of the Gazebo differential-drive robot. 

To start the simulator in a predefined environment, just run the following command:

```
ign gazebo -v 4 -r dialogos_robot.sdf
```

After a short loading sequence you should see a plain field and a little roboter.
Now start the ros-gz-bridge:

```
ros2 run ros_gz_bridge parameter_bridge /model/vehicle_blue/cmd_vel@geometry_msgs/msg/Twist]ignition.msgs.Twist
```

Now you can start the dialogsystem. The robot should now be controlled via your voice!

# What the project does

With the project you can use ROS2 within DialogOS. It is now possibile to send data via DialogOS to ROS2-topics and receive data of ROS2-topics in DialogOS. Our dialog can control a basic roboter to turn left/right and drive forwards/backwards. 

In the following graphic you can see our dialog. There is a big accordance between this and our initial concept from the beginning.

![final](/graphics/dialogos_final.jpg "final dialogos implementation")

# Outlook

In the future there could be a GPS and SLAM implementation for the roboter to drive exact distances and turn with exact degreenumbers. The implementation now can only control the the roboter for a certain time, not for exact distances/degreenumbers. 

# Known issues

The project can not run on a virtual machine, because there are some Networking/Port issues which we couldn't resolve in time. 
