source /opt/ros/humble/setup.bash


action1(){
	ros2 topic pub /online std_msgs/String "data: 'Roboter verfügbar'"
}

action2(){
	ign gazebo -v 4 -r dialogos_robot.sdf
}

action1 &
action2 &

wait

echo "Simulator shut down"
