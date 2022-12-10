#!/bin/bash
##
##   Copyright (c) 2006 by Thanos Kyritsis <djart@linux.gr>
##
##   This file is part of AL.I.S recording
##
##   AL.I.S recording is free software; you can redistribute it and/or modify
##   it under the terms of the GNU General Public License as published by
##   the Free Software Foundation, version 2 of the License.
##
##   AL.I.S recording is distributed in the hope that it will be useful,
##   but WITHOUT ANY WARRANTY; without even the implied warranty of
##   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
##   GNU General Public License for more details.
##
##   You should have received a copy of the GNU General Public License
##   along with AL.I.S recording; if not, write to the Free Software
##   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
##
##

VERSION=0.4.1
E_WRONGARGS=65
E_CARDNOTEXIST=66
E_FILENOTEXIST=67

if [ $# -eq 0 ]; then
	echo "Usage: `basename $0`  ALSA_CARD_NUMBER [FILE]"
	echo
	exit $E_WRONGARGS
fi

if [ -n "$2" ]; then
	TESTFILE=$2
else
	TESTFILE=silence.wav			  
fi

if [ -n "$1" ]; then
	cardexists=`arecord -l 2>&1 | grep "^card $1:"`
	if [ -z "$cardexists" ]; then
		echo "Card $1 does not exist. Aborting ..."
		echo
		exit $E_CARDNOTEXIST
	fi
fi

if [ ! -e  $TESTFILE ]; then
	echo "File $TESTFILE does not exist. Aborting ..."
	echo
	exit $E_FILENOTEXIST
fi

echo "Testing card: $1"
echo "Playing file: $TESTFILE"
aplay -q -D hw:$1 $TESTFILE &

echo
read -t 12 -p "Press SPACE to stop..." -d " "
echo

pid=`pidof -x aplay`
if [ -n "$pid" ]; then
	kill $pid
fi

exit 0
