#!/bin/bash

function makelinks {
	echo
	echo $1
	if [ -d "$1/src/com/growcontrol/" ]; then
		cd "$1/src/com/growcontrol/"
		if [ -L "gcCommon" ] || [ -d "gcCommon" ]; then
			echo "gcCommon already exists"
		else
			ln -s ../../../../gcCommon gcCommon
			ls -l --color=auto gcComm*
		fi
		cd ../../../../
	fi
	if [ -d "$1/" ]; then
		cd "$1/"
		if [ -L "lib" ] || [ -d "lib" ]; then
			echo "lib already exists"
		else
			ln -s ../lib lib
			ls -l --color=auto li*
		fi
		cd ../
	fi
}

makelinks "server"
makelinks "client"
echo

