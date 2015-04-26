### To see input devices (`cat /proc/bus/input/devices`).  Output looks like:
	I: Bus=0011 Vendor=0002 Product=0005 Version=0000
	N: Name="ImPS/2 Generic Wheel Mouse"
	P: Phys=isa0060/serio1/input0
	S: Sysfs=/devices/platform/i8042/serio1/input/input12
	U: Uniq=
	H: Handlers=mouse1 event12
	B: PROP=0
	B: EV=7
	B: KEY=70000 0 0 0 0
	B: REL=103

	I: Bus=0003 Vendor=046d Product=c247 Version=0110
	N: Name="Logitech G100s Optical Gaming Mouse"
	P: Phys=usb-0000:00:16.0-3/input0
	S: Sysfs=/devices/pci0000:00/0000:00:16.0/usb7/7-3/7-3:1.0/input/input16
	U: Uniq=
	H: Handlers=mouse0 event3
	B: PROP=0
	B: EV=17
	B: KEY=ff0000 0 0 0 0
	B: REL=103
	B: MSC=10

### Add Hue Jars to local repository:
	mvn install:install-file -Dfile=/tmp/huesdkresources.jar -DgroupId=com.philips.hue -DartifactId=hue-resources -Dversion=1.3.1 -Dpackaging=jar
    mvn install:install-file -Dfile=/tmp/huelocalsdk.jar -DgroupId=com.philips.hue -DartifactId=hue-sdk -Dversion=1.3.1 -Dpackaging=jar