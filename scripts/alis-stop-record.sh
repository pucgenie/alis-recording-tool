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

VERSION=0.5.2
E_WRONGARGS=65

if [ $# -eq 0 ]; then
	echo "Usage: `basename $0` -d DIRECTORY -t TIMESTAMP -s SEMINARIO -a AITHOUSA -sp SPEAKER"
	echo
	exit $E_WRONGARGS
fi

until [ -z "$1" ]
do
	if [ $1 == "-d" ]; then
		TARGET_DIR="$2"
		MAIN_PATH=`dirname $2`
		shift
	fi
	if [ $1 == "-t" ]; then
		TIMESTAMP="$2"
		shift
	fi
	if [ $1 == "-s" ]; then
		SEMINARIO="$2"
		shift
	fi
	if [ $1 == "-a" ]; then
		AITHOUSA="$2"
		shift
	fi
	if [ $1 == "-sp" ]; then
		SPEAKER="$2"
		shift
	fi
	shift
done

if [ -z "$SEMINARIO" ]; then
	SEMINARIO="SEMINARIO"
fi
if [ -z  "$AITHOUSA" ]; then
	AITHOUSA="AITHOUSA"
fi
if [ -z "$SPEAKER" ]; then
	SPEAKER="SPEAKER"
fi

# stop recording
pid=`pidof -x arecord`
if [ ! -z "$pid" ]; then
	echo "Killing: $pid"
	kill $pid
	sleep 2
else
	echo "arecord process not found, attempting killall ..."
	killall arecord
	sleep 2
fi

# Renaming
echo "Renaming $TARGET_DIR to $MAIN_PATH/alisrec_${TIMESTAMP}_${SEMINARIO}_${AITHOUSA}_${SPEAKER}"
mv $TARGET_DIR "${MAIN_PATH}/alisrec_${TIMESTAMP}_${SEMINARIO}_${AITHOUSA}_${SPEAKER}"
TARGET_DIR="${MAIN_PATH}/alisrec_${TIMESTAMP}_${SEMINARIO}_${AITHOUSA}_${SPEAKER}"

cd $TARGET_DIR
for file in `ls --color=no *.ogg`
do
	echo "Renaming $file to alisrec_${TIMESTAMP}_${SEMINARIO}_${AITHOUSA}_${SPEAKER}_${file}"
	mv $file "alisrec_${TIMESTAMP}_${SEMINARIO}_${AITHOUSA}_${SPEAKER}_${file}"
done

echo
exit 0
